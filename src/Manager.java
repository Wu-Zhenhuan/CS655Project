import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// equivalent to server
public class Manager {
    // info of workers, key: host name, value: port number
    protected static HashMap<String, Integer> workers;
    // worker connections, key: host name, value: connection thread
    protected static HashMap<String, ConnectionThread> connections;
    // the thread for the user
    protected static ConnectionThread userConnection;
    public static void setUserConnection(ConnectionThread ct) {userConnection = ct;}
    // main socket
    protected static ServerSocket managerSocket;
    // manager process
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 1) {
            Config.argInstruct("Manager", new String[] {"manager_port_number"});
            return;
        }
        // initialize manager socket
        try {
            managerSocket = new ServerSocket(Integer.parseInt(args[0]));
            Config.log("Manager socket started, port number: " + args[0]);
        }
        catch (Exception e) {e.printStackTrace(); return;}
        // initialize hash maps
        workers = new HashMap<>();
        connections = new HashMap<>();
        // create new threads as connections come
        while (true) {
            try {
                Socket acceptSocket = managerSocket.accept();
                ConnectionThread connectionThread = new ConnectionThread(acceptSocket);
                connectionThread.start();
            }
            catch (IOException ioe) {ioe.printStackTrace(); return;}
        }
    }
    // submit a job
    public static void submitJob(String md5Code) {
        // distribute workload
        int loadPerWorker = Config.ALPHABET.length/workers.size();
        // deal with the case where the workload cannot be evenly divided
        if (Config.ALPHABET.length%workers.size() != 0) {
            loadPerWorker++;
        }
        int itr = 0;  // iterate through the alphabet
        for (String hostName : connections.keySet()) {
            char start = Config.ALPHABET[itr];
            char end;
            // the last batch
            if (itr + loadPerWorker > Config.ALPHABET.length) {
                end = Config.ALPHABET[Config.ALPHABET.length - 1];
            }
            // an ordinary batch
            else {
                end = Config.ALPHABET[itr + loadPerWorker - 1];
            }
            // submit workload
            connections.get(hostName).out.println(start + "" + end + "" + md5Code);
            connections.get(hostName).out.flush();
            Config.log("Submitted workload for " + hostName + ": " + start + " - " + end + ", job: " + md5Code);
            itr += loadPerWorker;
        }
    }
    // add a worker
    public static void addWorker(String hostName, int portNumber, ConnectionThread connection) {
        workers.put(hostName, portNumber);
        connections.put(hostName, connection);
    }
    // remove a worker
    public static boolean delWorker(String hostName) {
        if (workers.containsKey(hostName)) {
            workers.remove(hostName);
            connections.get(hostName).out.println(Config.exitMsg);
            connections.remove(hostName);
            Config.log("Deleted worker: " + hostName);
            return true;
        }
        else {
            Config.log("No such worker to be deleted: " + hostName);
            return false;
        }
    }
    // display the info of currently connected workers
    public static String getWorkersInfo(boolean isShow) {
        StringBuilder info = new StringBuilder();
        if (workers.size() > 0) {
            info.append("----------------------------------------" + Config.infoDelim + "Worker(s) Info" + Config.infoDelim);
            int count = 1;
            for (String key : workers.keySet()) {
                info.append(count).append(". host name: ").append(key).append(", port number: ").append(workers.get(key)).append(Config.infoDelim);
                count++;
            }
            info.append("----------------------------------------");
        }
        else {
            info.append("No workers available.");
        }
        if (isShow) {
            System.out.println(info.toString().replaceAll(Config.infoDelim, "\n"));
        }
        return info.toString();
    }
}
