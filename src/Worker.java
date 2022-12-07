import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
// when a worker finds the answer, other workers should stop
public class Worker {
    protected HashMap<String, WorkerSession> sessions;
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 2) {return;}
        System.out.println("Cracker5MD5 Worker started!");
        Socket workerSocket;
        BufferedReader in;
        PrintWriter out;
        try {
            workerSocket = new Socket(args[0], Integer.parseInt(args[1]));
            in = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
            out = new PrintWriter(workerSocket.getOutputStream(), true);
            // tell the manager that this is a worker
            out.println("worker " + InetAddress.getLocalHost().getHostAddress() + " " + workerSocket.getLocalPort());
            out.flush();
            String ack;
            // wait for response
            do {ack = in.readLine();} while (ack == null);
            System.out.println("local print: " + ack);
        }
        catch (IOException ioe) {ioe.printStackTrace(); return;}
        try {
            System.out.println("Worker: " + InetAddress.getLocalHost().getHostAddress() + " " + workerSocket.getLocalPort());
            System.out.println("Manager: " + args[0] + " " + args[1]);
        }
        catch (UnknownHostException uhe) {uhe.printStackTrace(); return;}
        String workerOutput = "ans workerOutput";
        while (true) {
            try {
                String managerMsg;
                // wait for response
                do {managerMsg = in.readLine();} while (managerMsg == null);
                managerMsg = managerMsg.trim();
                System.out.println("local print: " + managerMsg);
                if (managerMsg.length() == 34) {
                    out.println(workerOutput);
                    out.flush();
                }
                if (managerMsg.equalsIgnoreCase("exit")) {return;}
            }
            catch (IOException ioe) {ioe.printStackTrace();}
        }
    }
}
