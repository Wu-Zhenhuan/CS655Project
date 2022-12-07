import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// equivalent to server
public class Manager {
    // key: host name, value: port number
    protected static HashMap<String, Integer> workers;
    // key: host name, value: connection thread
    protected static HashMap<String, ConnectionThread> connections;
    // the thread for the user
    protected static ConnectionThread userConnection;
    public static void setUserConnection(ConnectionThread ct) {userConnection = ct;}
    // main socket
    protected static ServerSocket managerSocket;
    // manager process
    public static void main(String[] args) {
        // check the validity of arguments
        if (args.length != 1) {System.err.println("Invalid argument."); return;}
        // initialize manager socket
        try {
            managerSocket = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Manager socket started, port number: " + args[0]);
        }
        catch (Exception e) {e.printStackTrace(); return;}
        // initialize hash maps
        workers = new HashMap<>();
        connections = new HashMap<>();
        // create new threads as connections come
        while (true) {
            try {
                Socket acceptSocket = managerSocket.accept();
                System.out.println("connection established");
                ConnectionThread connectionThread = new ConnectionThread(acceptSocket);
                connectionThread.start();
            }
            catch (IOException ioe) {ioe.printStackTrace();}
        }
        /*
        // read what the user types
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput = "";
        // handle general user input, constantly prompt for input
        while (true) {
            // accept incoming connections
            try {
                Socket acceptSocket = managerSocket.accept();
                ConnectionThread connectionThread = new ConnectionThread(acceptSocket);
                connectionThread.start();
            }
            catch (IOException ioe) {ioe.printStackTrace();}
            // input prompt
            System.out.println(inputPrompt);
            try {userInput = stdIn.readLine().trim();}
            catch (IOException ioe) {ioe.printStackTrace();}
            // exit the manager
            if (userInput.equalsIgnoreCase(exitMsg)) {
                for (String key : connections.keySet()) {connections.get(key).out.println(exitMsg);}
                return;
            }
            // show worker info
            else if (userInput.equalsIgnoreCase(infoMsg)) {showWorkersInfo();}
            // handle complex commands
            else {
                // split the commands with white-spaces as delimiters
                String[] command = userInput.split("\\s+");
                System.out.println("debug: command: " + Arrays.toString(command));
                // add a new worker, initiate a new thread
                if (command.length == 3 && command[0].equalsIgnoreCase(addMsg)) {
                    String hostName = command[1];
                    int portNumber = Integer.parseInt(command[2]);
                    Socket tcpSocket;
                    try {
                        tcpSocket = new Socket(hostName, portNumber);
                        System.out.println("debug: create closed " + tcpSocket.isClosed());
                        ConnectionThread connection = new ConnectionThread(tcpSocket);
                        addWorker(hostName, portNumber, connection);
                        (new Thread(connection)).start();
                    }
                    catch (Exception e) {e.printStackTrace();}
                }
                // remove a worker
                else if (command.length == 2 && command[0].equalsIgnoreCase(delMsg)) {delWorker(command[1]);}
                // submit a job to workers
                else if (command.length == 2 && command[0].equalsIgnoreCase(crackMsg) && command[1].length() == md5Len) {
                    // distribute workload
                    int loadPerWorker = WorkerSession.ALPHABET.length/workers.size();
                    // the workload cannot be evenly divided
                    if (WorkerSession.ALPHABET.length%workers.size() != 0) {
                        loadPerWorker++;
                    }
                    int itr = 0;  // iterate through the alphabet
                    for (String key : connections.keySet()) {
                        char start = WorkerSession.ALPHABET[itr];
                        char end;
                        // the last batch
                        if (itr + loadPerWorker > WorkerSession.ALPHABET.length) {
                            end = WorkerSession.ALPHABET[WorkerSession.ALPHABET.length - 1];
                        }
                        // an ordinary batch
                        else {
                            end = WorkerSession.ALPHABET[itr + loadPerWorker - 1];
                        }
                        // submit workload
                        connections.get(key).out.println(start + end + command[1]);
                        connections.get(key).out.flush();
                        System.out.println("Workload for " + key + ": " + start + " - " + end);
                        System.out.println("debug: closed? " + connections.get(key).socket.isClosed());
                        itr += loadPerWorker;
                    }
                }
                else {
                    System.err.println("Invalid command.");
                }
            }
        }*/
    }
    // submit a job
    public static void submit(String md5Code) {
        // distribute workload
        int loadPerWorker = Config.ALPHABET.length/workers.size();
        // deal with the case where the workload cannot be evenly divided
        if (Config.ALPHABET.length%workers.size() != 0) {
            loadPerWorker++;
        }
        int itr = 0;  // iterate through the alphabet
        for (String key : connections.keySet()) {
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
            connections.get(key).out.println(start + "" + end + "" + md5Code);
            connections.get(key).out.flush();
            System.out.println("Workload for " + key + ": " + start + " - " + end);
            System.out.println("debug: closed? " + connections.get(key).socket.isClosed());
            itr += loadPerWorker;
        }
    }
    // add a worker
    public static void addWorker(String hostName, int portNumber, ConnectionThread connection) {
        workers.put(hostName, portNumber);
        connections.put(hostName, connection);
    }
    // remove a worker
    public static void delWorker(String hostName) {
        if (workers.containsKey(hostName)) {
            workers.remove(hostName);
            connections.get(hostName).out.println(Config.exitMsg);
            connections.remove(hostName);
        }
        else {
            System.err.println("No such worker to be deleted: " + hostName);
        }
    }
    // display the info of currently connected workers
    public static String getWorkersInfo(boolean isShow) {
        StringBuilder info = new StringBuilder();
        info.append("----------------------------------------@Worker(s) Info@");
        int count = 1;
        for (String key : workers.keySet()) {
            info.append(count).append(". host name: ").append(key).append(", port number: ").append(workers.get(key)).append("@");
            count++;
        }
        info.append("----------------------------------------");
        if (isShow) {System.out.println(info.toString().replaceAll("@", "\n"));}
        return info.toString();
    }
}
