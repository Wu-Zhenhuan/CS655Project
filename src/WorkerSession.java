import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WorkerSession extends Thread {
    protected static MessageDigest md;
    static {
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    protected String answer;
    private final String cypher;
    private final int start;
    private final int end;
    public WorkerSession(String cypher, int start, int end) {
        answer = "";
        this.cypher = cypher.toLowerCase();
        this.start = start;
        this.end = end;
    }
    @Override
    public void run() {
        System.out.println("First character range: from '" + Config.ALPHABET[start] + "' to '" + Config.ALPHABET[end] + "'");
        System.out.println("Cypher: " + cypher);
        answer = crack(start, end, cypher);
        if (answer == null) {
            System.err.println("Unable to crack!");
        }
        else {
            System.out.println("Plain: " + answer);
        }
    }
    // find the index of the given character in the alphabet
    public static int getIndex (char ch) {
        if (('A' <= ch) && (ch <= 'Z')) {return (int) ch - 65;}
        else if (('a' <= ch) && (ch <= 'z')) {return (int) ch - 71;}
        else {return -1;}
    }
    // handle the workload assigned to this worker
    public static String crack(int start, int end, String cypher) {
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < Config.ALPHABET.length; j++) {
                for (int k = 0; k < Config.ALPHABET.length; k++) {
                    for (int l = 0; l < Config.ALPHABET.length; l++) {
                        for (int m = 0; m < Config.ALPHABET.length; m++) {
                            // obtain the combination of the characters
                            String combo = Config.ALPHABET[i] + "" + Config.ALPHABET[j] + "" +
                                           Config.ALPHABET[k] + "" + Config.ALPHABET[l] + "" + Config.ALPHABET[m];
                            // convert to MD5 code
                            byte[] pre = combo.getBytes(StandardCharsets.UTF_8);
                            byte[] hash = md.digest(pre);
                            BigInteger numerical = new BigInteger(1, hash);
                            String string = numerical.toString(16);
                            // check if the MD5 code matches
                            if (string.equals(cypher)) {
                                return combo;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
