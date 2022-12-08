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
                String displayedMsg = "Manager response: \n";
                // record the time taken for cracking one password
                String[] segments = managerMsg.split(Config.whiteSpace);
                if (segments.length == 5 && segments[0].equalsIgnoreCase(Config.ansMsg)) {
                    // {ans, plain-text, cypher, IP, port}
                    displayedMsg += "Successfully cracked: job: " + segments[2] + ", answer: " + segments[1]
                            + ", from worker: (" + segments[3] + ", " + segments[4] + "), time-span: "
                            + (timeNow - User.responseTime.get(segments[2]))/1000000000d + " seconds";
                    User.responseTime.remove(segments[2]);
                }
                else if (segments.length == 2 && segments[0].equalsIgnoreCase(Config.failMsg)) {
                    displayedMsg += "Unable to crack: " + segments[1] + ", time-span: "
                            + (timeNow - User.responseTime.get(segments[1]))/1000000000d + " seconds";
                    User.responseTime.remove(segments[1]);
                }
                // ordinary message
                else {
                    displayedMsg += managerMsg;
                }
                // buffer the manager's messages
                msgBuffer.add(displayedMsg);
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
