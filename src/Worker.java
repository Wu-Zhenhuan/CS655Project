import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
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
            Config.argInstruct("Worker", new String[] {"manager_address", "manager_port_number"});
            return;
        }
        sessions = new HashMap<>();
        String workerHost;
        int workerPort;
        try {
            workerSocket = new Socket(args[0], Integer.parseInt(args[1]));
            in = new BufferedReader(new InputStreamReader(workerSocket.getInputStream()));
            out = new PrintWriter(workerSocket.getOutputStream(), true);
            // tell the manager that this is a worker
            Config.log("Worker started!");
            workerHost = InetAddress.getLocalHost().getHostAddress();
            workerPort = workerSocket.getLocalPort();
            out.println(Config.workerMsg + " " + workerHost + " " + workerPort);
            out.flush();
            String ack;
            // wait for response
            do {ack = in.readLine();} while (ack == null);
            Config.log(ack);
        }
        catch (IOException ioe) {ioe.printStackTrace(); return;}
        Config.log("Worker: " + workerHost + ", " + workerPort + "; Manager: " + args[0] + ", " + args[1]);
        while (true) {
            try {
                String managerMsg;
                // wait for response
                do {managerMsg = in.readLine();} while (managerMsg == null);
                managerMsg = managerMsg.trim();
                Config.log("Received: " + managerMsg);
                String[] segments = managerMsg.split(Config.whiteSpace);
                // receive a job
                if (managerMsg.length() == Config.md5Len + 2) {
                    String md5Cypher = managerMsg.substring(2);
                    Config.log("New job: " + md5Cypher);
                    WorkerSession ws = new WorkerSession(md5Cypher, Config.getIndex(managerMsg.charAt(0)),
                                                         Config.getIndex(managerMsg.charAt(1)), workerHost, workerPort);
                    ws.start();
                    sessions.put(md5Cypher, ws);
                }
                // receive an order to stop a job
                else if (segments.length == 2 && segments[0].equalsIgnoreCase(Config.stopMsg)) {
                    sessions.get(segments[1]).kill();
                    sessions.remove(segments[1]);
                    Config.log("Stopped job: " + segments[1]);
                }
                // terminate the worker
                else if (managerMsg.equalsIgnoreCase(Config.exitMsg)) {
                    in.close();
                    out.close();
                    workerSocket.close();
                    Config.log("Exited worker.");
                    return;
                }
            }
            catch (IOException ioe) {ioe.printStackTrace(); return;}
        }
    }
}
