import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

// listen the messages from the manager
public class UserListener extends Thread {
    protected Queue<String> msgBuffer;
    public UserListener() {
        msgBuffer = new LinkedList<>();
    }
    @Override
    public void run() {
        long timeNow;
        while (true) {
            try {
                // wait for response
                String managerMsg;
                do {
                    managerMsg = User.in.readLine();
                    timeNow = System.nanoTime();
                } while (managerMsg == null);
                managerMsg = managerMsg.trim();
                // deal with the formatting in case of workers' info
                if (managerMsg.startsWith("----")) {
                    managerMsg = managerMsg.replaceAll(Config.infoDelim, "\n");
                }
                String msg = "manager response: \n" + managerMsg;
                // record the time taken for cracking one password
                if (managerMsg.startsWith("answer")) {
                    String[] segments = managerMsg.split(Config.whiteSpace);
                    msg += " span: " + (timeNow - User.responseTime.get(segments[4]))/1000000000d + " seconds";
                    User.responseTime.remove(segments[4]);
                }
                // buffer the manager's messages
                msgBuffer.add(msg);
                System.out.println("debug size " + msgBuffer.size());
                // show the messages from the manager
                while (msgBuffer.size() > 0) {
                    System.out.println(msgBuffer.poll());
                    if (msgBuffer.size() == 0) {
                        System.out.println(Config.inputPrompt);
                    }
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }
    }
}
