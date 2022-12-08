import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
            System.err.println("Invalid argument. User <manager address> <manager port number>");
            return;
        }
        try {
            userSocket = new Socket(args[0], Integer.parseInt(args[1]));
            userInput = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            out = new PrintWriter(userSocket.getOutputStream(), true);
            // tell the manager that this is a user
            out.println("user " + InetAddress.getLocalHost().getHostAddress() + " " + userSocket.getLocalPort());
            out.flush();
            String ack;
            // wait for response
            do {ack = in.readLine();} while (ack == null);
            System.out.println("local print: " + ack);
        }
        catch (IOException ioe) {ioe.printStackTrace(); return;}
        try {
            System.out.println("User: " + InetAddress.getLocalHost().getHostAddress() + " " + userSocket.getLocalPort());
            System.out.println("Manager: " + args[0] + " " + args[1]);
        }
        catch (UnknownHostException uhe) {uhe.printStackTrace(); return;}
        String inputLine;
        responseTime = new HashMap<>();
        UserListener userListener = new UserListener();
        userListener.start();
        System.out.println(Config.inputPrompt);
        while (true) {
            try {
                inputLine = userInput.readLine().trim();
                if (inputLine.startsWith(Config.crackMsg)) {
                    String cypher = inputLine.substring(6);
                    if (responseTime.containsKey(cypher)) {
                        System.out.println("You have submitted before: " + cypher);
                    }
                    else {
                        responseTime.put(cypher, System.nanoTime());
                        out.println(inputLine);
                        out.flush();
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
                    return;
                }
            }
            catch (IOException e) {e.printStackTrace(); return;}
        }
    }
}
