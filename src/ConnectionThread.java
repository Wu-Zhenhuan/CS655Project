import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionThread extends Thread {
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected HashMap<String, Integer> failures;
    public ConnectionThread(Socket socket) {
        this.socket = socket;
        failures = new HashMap<>();
    }
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String workerHostAddress = socket.getInetAddress().getHostAddress();
            int workerPortNum = socket.getPort();
            Config.log("New connection thread: address: " + workerHostAddress + ", port number: " + workerPortNum);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        // process the incoming messages
        String nextLine;
        while (true) {
            try {
                nextLine = in.readLine();
                if (nextLine != null) {
                    nextLine = nextLine.trim();
                    Config.log("Received message: " + nextLine);
                    String[] command = nextLine.split(Config.whiteSpace);
                    // exit
                    if (command.length == 1 && nextLine.equalsIgnoreCase(Config.exitMsg)) {
                        // tell the workers to exit
                        for (String keyHost : Manager.connections.keySet()) {
                            Manager.connections.get(keyHost).out.println(Config.exitMsg);
                            Manager.connections.get(keyHost).out.flush();
                            Config.log("Worker exited: " + keyHost);
                        }
                        // safe close
                        in.close();
                        out.close();
                        socket.close();
                        Manager.managerSocket.close();
                        Manager.setUserConnection(null);
                        Config.log("Exited manager.");
                        return;
                    }
                    // show workers' info
                    else if (command.length == 1 && nextLine.equalsIgnoreCase(Config.infoMsg)) {
                        out.println(Manager.getWorkersInfo(false));
                        out.flush();
                    }
                    // delete a worker
                    // {del, IP}
                    else if (command.length == 2 && command[0].equalsIgnoreCase(Config.delMsg)) {
                        if (Manager.delWorker(command[1])) {
                            out.println("Deleted worker: " + command[1]);
                        }
                        else {
                            out.println("Failed to delete worker: " + command[1]);
                        }
                        out.flush();
                    }
                    // submit job to worker(s)
                    // {crack, MD5}
                    else if (command.length == 2 && command[0].equalsIgnoreCase(Config.crackMsg) && command[1].length() == Config.md5Len) {
                        // we have workers available
                        if (Manager.workers.size() > 0) {
                            Manager.submitJob(command[1]);
                        }
                        // tell the user that there is no worker
                        else {
                            Config.log("No workers connected.");
                            out.println(Manager.getWorkersInfo(false));
                            out.flush();
                        }
                    }
                    // response from a worker: stop thread
                    // {stop, cypher, IP, port}
                    else if (command.length == 4 && command[0].equalsIgnoreCase(Config.stopMsg)) {
                        Config.log("Worker stopped: job: " + command[1] + ", host: " + command[2] + ", port: " + command[3]);
                    }
                    // response from a worker: failure
                    // {ans, cypher, IP, port}
                    else if (command.length == 4 && command[0].equalsIgnoreCase(Config.ansMsg)) {
                        Config.log("Failure from worker: job: " + command[1] + ", host: " + command[2] + ", port: " + command[3]);
                        // count the number of workers who failed to crack this password
                        if (Manager.userConnection.failures.containsKey(command[1])) {
                            Manager.userConnection.failures.put(command[1], Manager.userConnection.failures.get(command[1]) + 1);
                        }
                        else {
                            Manager.userConnection.failures.put(command[1], 1);
                        }
                        // if all the workers failed to crack this password, send the message to the user
                        if (Manager.userConnection.failures.get(command[1]) == Manager.workers.size()) {
                            Manager.userConnection.out.println(Config.failMsg + " " + command[1]);
                            Manager.userConnection.out.flush();
                            Manager.userConnection.failures.remove(command[1]);
                            Config.log("All failed: " + command[1]);
                        }
                    }
                    // response from a worker: success
                    // {ans, plain-text, cypher, IP, port}
                    else if (command.length == 5 && command[0].equalsIgnoreCase(Config.ansMsg)) {
                        // tell other workers to stop cracking this password
                        for (String keyHost : Manager.connections.keySet()) {
                            Manager.connections.get(keyHost).out.println(Config.stopMsg + " " + command[2]);
                            Manager.connections.get(keyHost).out.flush();
                            Config.log("Stopped worker: " + keyHost + ", job: " + command[2]);
                        }
                        // output the cracked answer
                        Manager.userConnection.out.println(Config.ansMsg + " " + command[1] + " " + command[2] + " " + command[3] + " " + command[4]);
                        Manager.userConnection.out.flush();
                    }
                    // incoming user
                    // {user, IP, port}
                    else if (command.length == 3 && command[0].equalsIgnoreCase(Config.userMsg)) {
                        // save this user connection
                        Manager.setUserConnection(this);
                        Config.log("Registered user: " + command[1] + ", " + command[2]);
                        out.println("Manager registered user: " + command[1] + ", " + command[2]);
                        out.flush();
                    }
                    // incoming worker
                    // {worker, IP, port}
                    else if (command.length == 3 && command[0].equalsIgnoreCase(Config.workerMsg)) {
                        // save this worker connection
                        Manager.addWorker(command[1], Integer.parseInt(command[2]), this);
                        Config.log("Registered worker: " + command[1] + ", " + command[2]);
                        out.println("Manager registered worker: " + command[1] + ", " + command[2]);
                        out.flush();
                    }
                    else {
                        out.println("Invalid input!");
                        out.flush();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
