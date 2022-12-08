import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// a worker process thread dedicated to deal with a particular workload
public class WorkerSession extends Thread {
    // handle the conversion of MD5
    protected MessageDigest messageDigest;
    // the result of cracking
    protected String answer;
    // the MD5 code of the actual password
    private final String cypher;
    // workload range
    private final int start;
    private final int end;
    // socket info
    private final String hostAddress;
    private final int portNum;
    // whether to continue this thread
    private boolean isRun;
    // constructor
    public WorkerSession(String cypher, int start, int end, String hostAddress, int portNum) {
        try {messageDigest = MessageDigest.getInstance("MD5");}
        catch (NoSuchAlgorithmException e) {throw new RuntimeException(e);}
        answer = "";
        this.cypher = cypher.toLowerCase();
        this.start = start;
        this.end = end;
        this.hostAddress = hostAddress;
        this.portNum = portNum;
        isRun = true;
    }
    // run the thread
    @Override
    public void run() {
        Config.log("Start cracking: cypher: " + cypher + ", 1st character range: " + Config.ALPHABET[start] + " - " + Config.ALPHABET[end]);
        answer = crack();
        // stop thread
        if (!isRun) {
            Config.log("Thread stop: " + cypher);
            Worker.out.println(Config.stopMsg + " " + cypher + " " + hostAddress + " " + portNum);
        }
        // output answer
        else {
            if (answer != null) {
                Config.log("Cracked: " + cypher + " -> " + answer);
                Worker.out.println(Config.ansMsg + " " + answer + " " + cypher + " " + hostAddress + " " + portNum);
            }
            else {
                Config.log("Cannot crack: " + cypher);
                Worker.out.println(Config.ansMsg + " " + cypher + " " + hostAddress + " " + portNum);
            }
        }
        Worker.out.flush();
    }
    // stop the thread
    public void kill() {
        isRun = false;
    }
    // handle the workload assigned to this worker
    public String crack() {
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < Config.ALPHABET.length; j++) {
                for (int k = 0; k < Config.ALPHABET.length; k++) {
                    for (int l = 0; l < Config.ALPHABET.length; l++) {
                        for (int m = 0; m < Config.ALPHABET.length; m++) {
                            if (isRun) {
                                // obtain the combination of the characters
                                String combo = Config.ALPHABET[i] + "" + Config.ALPHABET[j] + "" +
                                        Config.ALPHABET[k] + "" + Config.ALPHABET[l] + "" + Config.ALPHABET[m];
                                // convert to MD5 code
                                byte[] pre = combo.getBytes(StandardCharsets.UTF_8);
                                byte[] hash = messageDigest.digest(pre);
                                BigInteger numerical = new BigInteger(1, hash);
                                String string = numerical.toString(16);
                                // check if the MD5 code matches
                                if (string.equals(cypher)) {
                                    return combo;
                                }
                            }
                            else {break;}
                        }
                    }
                }
            }
        }
        return null;
    }
}
