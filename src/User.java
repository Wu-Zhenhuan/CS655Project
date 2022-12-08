import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class User {
    protected static Socket userSocket;
    protected static BufferedReader userInput;
    protected static BufferedReader in;
    protected static PrintWriter out;
    protected static HashMap<String, Long> responseTime;
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 2) {
            Config.argInstruct("User", new String[] {"manager_address", "manager_port_number"});
            return;
        }
        String userHost;
        int userPort;
        try {
            userSocket = new Socket(args[0], Integer.parseInt(args[1]));
            userInput = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            out = new PrintWriter(userSocket.getOutputStream(), true);
            Config.log("User started!");
            // tell the manager that this is a user
            userHost = InetAddress.getLocalHost().getHostAddress();
            userPort = userSocket.getLocalPort();
            out.println(Config.userMsg + " " + userHost + " " + userPort);
            out.flush();
            // wait for response
            String ack;
            do {ack = in.readLine();} while (ack == null);
            Config.log(ack);
        }
        catch (IOException ioe) {ioe.printStackTrace(); return;}
        Config.log("User: " + userHost + ", " + userPort + "; Manager: " + args[0] + ", " + args[1]);
        String inputLine;
        // record the response time for cracking passwords
        responseTime = new HashMap<>();
        // listen to the manager
        UserListener userListener = new UserListener();
        userListener.start();
        Config.log("Start listening to the manager...");
        System.out.println(Config.inputPrompt);
        while (true) {
            try {
                // user input
                inputLine = userInput.readLine().trim();
                // check input validity
                if (!checkInput(inputLine)) {
                    System.err.println("Invalid input.\n" + Config.inputPrompt);
                }
                else {
                    String[] segments = inputLine.split(Config.whiteSpace);
                    if (segments.length == 2 && segments[0].equalsIgnoreCase(Config.crackMsg)) {
                        if (responseTime.containsKey(segments[1])) {
                            System.err.println("You have submitted " + segments[1] + " before.");
                        }
                        else {
                            responseTime.put(segments[1], System.nanoTime());
                            out.println(inputLine);
                            out.flush();
                            Config.log("Submitted job: " + segments[1]);
                        }
                    }
                    else {
                        out.println(inputLine);
                        out.flush();
                    }
                    // quit the program
                    if (inputLine.equalsIgnoreCase(Config.exitMsg)) {
                        in.close();
                        out.close();
                        userInput.close();
                        userSocket.close();
                        Config.log("Exited user.");
                        return;
                    }
                }
            }
            catch (IOException e) {return;}
        }
    }
    // there are only 4 valid commands: exit, info, del, crack
    public static boolean checkInput(String ipt) {
        if (!ipt.equalsIgnoreCase(Config.exitMsg) && !ipt.equalsIgnoreCase(Config.infoMsg)) {
            String[] segments = ipt.split(Config.whiteSpace);
            if (segments.length != 2) {return false;}
            return (segments[0].equalsIgnoreCase(Config.delMsg) ||
                    (segments[0].equalsIgnoreCase(Config.crackMsg) && segments[1].length() == Config.md5Len));
        }
        return true;
    }
}
