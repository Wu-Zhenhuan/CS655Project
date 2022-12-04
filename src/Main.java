import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Cracker5MD5 Terminal started!");
        String hostName;
        int portNumber;
        if (args.length >= 2) {
            System.out.println("Server   IP = " + args[0]);
            System.out.println("Port Number = " + args[1]);

            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        } else {
            System.out.println("Please use first command-line argument to set sever IP address and use second argument to set port number.");
            return;
        }

        try (
                Socket tcpSocket = new Socket(hostName, portNumber);        // 1st statement
                PrintWriter out =                                            // 2nd statement
                        new PrintWriter(tcpSocket.getOutputStream(), true);

                BufferedReader in =                                          // 3rd statement
                        new BufferedReader(
                                new InputStreamReader(tcpSocket.getInputStream()));
                BufferedReader stdIn =                                       // 4th statement
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                out.flush();
                System.out.println("Client sent: " + userInput);
                System.out.println("Waiting for the result...");
                while (!in.ready()) {

                }
                System.out.println(in.readLine());
            }

        } catch (Exception e) {
            System.err.println("Echo Client exits at main() due to the following:");
            e.printStackTrace();
            System.exit(0);
        }
    }
}