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
            System.out.println("Worker has been connected!");
            System.out.println("Input must be [start char][end char][MD5 Hash of 32 chars].\nType 'exit' to quit.");
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                String line = userInput.trim();
                if (line.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    return;
                }
                if (line.length() != 34) {
                    System.err.println("Wrong format!\nInput must be [start char][end char][MD5 Hash of 32 chars].\nType 'exit' to quit.");
                    continue;
                }
                out.println(line);
                out.flush();
                System.out.println("Client sent: " + line);
                System.out.println("Waiting for confirmation from worker...");
                String confirmation;
                while ((confirmation = in.readLine())==null) {

                }
                System.out.println(confirmation);
                while ((confirmation = in.readLine())==null) {

                }
                System.out.println(confirmation);
                System.out.println("Waiting for result from worker...");
                String result;
                while ((result = in.readLine())==null) {

                }
                System.out.println(result);
            }
        } catch (Exception e) {
            System.err.println("Echo Client exits at main() due to the following:");
            e.printStackTrace();
            System.exit(0);
        }
    }
}