import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class User {
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 2) {return;}
        Socket userSocket;
        BufferedReader userInput;
        BufferedReader in;
        PrintWriter out;
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
        while (true) {
            try {
                System.out.println("Input: ");
                inputLine = userInput.readLine().trim();
                out.println(inputLine);
                out.flush();
                String managerMsg;
                // wait for response
                do {managerMsg = in.readLine();} while (managerMsg == null);
                managerMsg = managerMsg.trim();
                if (inputLine.equals(Config.infoMsg)) {
                    managerMsg = managerMsg.replaceAll("@", "\n");
                }
                System.out.println("manager response: \n" + managerMsg);
                if (managerMsg.equalsIgnoreCase("exit")) {return;}
            }
            catch (IOException ioe) {ioe.printStackTrace();}
        }
    }
}
