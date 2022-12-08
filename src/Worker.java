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
    // key: MD5 code, value: worker thread
    protected static HashMap<String, WorkerSession> sessions;
    // worker socket
    protected static Socket workerSocket;
    // incoming message
    protected static BufferedReader in;
    // output message
    protected static PrintWriter out;
    // main process
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 2) {
            System.err.println("Invalid argument. Worker <manager address> <manager port number>");
            return;
        }
        sessions = new HashMap<>();
        try {
            workerSocket = new Socket(args[0], Integer.parseInt(args[1]));
            in = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
            out = new PrintWriter(workerSocket.getOutputStream(), true);
            // tell the manager that this is a worker
            System.out.println("Cracker5MD5 Worker started!");
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
        while (true) {
            try {
                String managerMsg;
                // wait for response
                do {managerMsg = in.readLine();} while (managerMsg == null);
                managerMsg = managerMsg.trim();
                System.out.println("local print: " + managerMsg);
                if (managerMsg.length() == 34) {
                    String md5Cypher = managerMsg.substring(2);
                    WorkerSession ws = new WorkerSession(md5Cypher, Config.getIndex(managerMsg.charAt(0)),
                                                         Config.getIndex(managerMsg.charAt(1)),
                                                         InetAddress.getLocalHost().getHostAddress(),
                                                         workerSocket.getLocalPort());
                    ws.start();
                    sessions.put(md5Cypher, ws);
                }
                else if (managerMsg.length() > 4 && managerMsg.substring(0, 4).equalsIgnoreCase("stop")) {
                    String jobCode = managerMsg.substring(5);
                    System.out.println("DEBUG: " + jobCode);
                    sessions.get(jobCode).kill();
                    sessions.remove(jobCode);
                }
                // terminate the worker
                else if (managerMsg.equalsIgnoreCase(Config.exitMsg)) {return;}
            }
            catch (IOException ioe) {ioe.printStackTrace(); return;}
        }
    }
}
