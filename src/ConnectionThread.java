import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionThread extends Thread {
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    public ConnectionThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String workerHostAddress = socket.getInetAddress().getHostAddress();
            int workerPortNum = socket.getPort();
            System.out.println("connected: " + workerHostAddress + " " + workerPortNum);
            //Manager.addWorker(workerHostName, workerPortNum, this);
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
                    String[] command = nextLine.split("\\s+");
                    // exit
                    if (command.length == 1 && nextLine.equalsIgnoreCase(Config.exitMsg)) {
                        //Manager.managerSocket.close();
                        Manager.setUserConnection(null);
                        return;
                    }
                    // show workers' info
                    else if (command.length == 1 && nextLine.equalsIgnoreCase(Config.infoMsg)) {
                        out.println(Manager.getWorkersInfo(false));
                        out.flush();
                    }
                    // delete a worker
                    else if (command.length == 2 && command[0].equalsIgnoreCase(Config.delMsg)) {
                        if (Manager.delWorker(command[1])) {
                            out.println("deleted " + command[1]);
                        }
                        else {
                            out.println("cannot delete " + command[1]);
                        }
                        out.flush();
                    }
                    // submit job to worker(s)
                    else if (command.length == 2 && command[0].equalsIgnoreCase(Config.crackMsg) && command[1].length() == Config.md5Len) {
                        // we have workers available
                        if (Manager.workers.size() > 0) {
                            Manager.submitJob(command[1]);
                        }
                        // tell the user that there is no worker
                        else {
                            out.println(Manager.getWorkersInfo(false));
                            out.flush();
                        }
                    }
                    // response from a worker: failure
                    else if (command.length == 3 && command[0].equalsIgnoreCase("ans")) {
                        System.out.println("failure from worker: " + command[1] + " " + command[2]);
                    }
                    // response from a worker: success
                    else if (command.length == 5 && command[0].equalsIgnoreCase("ans")) {
                        // tell other workers to stop cracking this password
                        for (String key : Manager.connections.keySet()) {
                            Manager.connections.get(key).out.println("stop " + command[2]);
                            Manager.connections.get(key).out.flush();
                            System.out.println("stopped " + key);
                        }
                        // output the cracked answer
                        Manager.userConnection.out.println("answer from worker: " + command[1] + " " + command[2] + " " + command[3] + " " + command[4]);
                    }
                    // incoming user
                    else if (command.length == 3 && command[0].equalsIgnoreCase("user")) {
                        out.println("manager ack user " + command[1] + " " + command[2]);
                        out.flush();
                        // save this user connection
                        Manager.setUserConnection(this);
                    }
                    // incoming worker
                    else if (command.length == 3 && command[0].equalsIgnoreCase("worker")) {
                        out.println("manager ack worker " + command[1] + " " + command[2]);
                        out.flush();
                        // save this worker connection
                        Manager.addWorker(command[1], Integer.parseInt(command[2]), this);
                    }
                    else {
                        out.println(nextLine);
                        out.flush();
                    }
                    System.out.println("local print: " + nextLine);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
