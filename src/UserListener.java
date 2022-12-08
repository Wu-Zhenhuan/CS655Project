import java.io.IOException;
// listen the messages from the manager
public class UserListener extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                // wait for response
                String managerMsg;
                do {managerMsg = User.in.readLine();} while (managerMsg == null);
                managerMsg = managerMsg.trim();
                // deal with the formatting in case of workers' info
                if (managerMsg.length() > 4 && managerMsg.startsWith("----")) {
                    managerMsg = managerMsg.replaceAll(Config.infoDelim, "\n");
                }
                // show the manager's response
                System.out.println("manager response: \n" + managerMsg);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }
    }
}
