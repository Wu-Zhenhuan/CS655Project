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
                        Manager.managerSocket.close();
                        return;
                    }
                    // show workers' info
                    else if (command.length == 1 && nextLine.equalsIgnoreCase(Config.infoMsg)) {
                        out.println(Manager.getWorkersInfo(false));
                        out.flush();
                    }
                    // delete a worker
                    if (command.length == 2 && command[0].equalsIgnoreCase(Config.delMsg)) {

                    }
                    // submit job to worker(s)
                    else if (command.length == 2 && command[0].equalsIgnoreCase(Config.crackMsg) && command[1].length() == Config.md5Len) {
                        Manager.submit(command[1]);
                    }
                    // an answer from a worker
                    else if (command.length == 2 && command[0].equalsIgnoreCase("ans")) {
                        Manager.userConnection.out.println("answer from worker: " + command[1]);
                    }
                    // incoming user
                    if (command.length == 3 && command[0].equalsIgnoreCase("user")) {
                        out.println("manager ack user " + command[1] + " " + command[2]);
                        out.flush();
                        Manager.setUserConnection(this);
                    }
                    // incoming worker
                    else if (command.length == 3 && command[0].equalsIgnoreCase("worker")) {
                        out.println("manager ack worker " + command[1] + " " + command[2]);
                        out.flush();
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
