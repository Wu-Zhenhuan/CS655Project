// collection of commands & configurations
public abstract class Config {
    // length for an MD5 code
    protected static final int md5Len = 32;
    // exit the program
    protected static final String exitMsg = "exit";
    // show worker info
    protected static final String infoMsg = "info";
    // info delimiter
    protected static final String infoDelim = "@";
    // delete worker
    protected static final String delMsg = "del";
    // crack password
    protected static final String crackMsg = "crack";
    // stop a worker
    protected static final String stopMsg = "stop";
    // crack failure
    protected static final String failMsg = "fail";
    // logging
    protected static final String logMsg = "LOG: ";
    public static void log(String msg) {
        System.out.println(logMsg + msg);
    }
    // identity message
    protected static final String userMsg = "user";
    protected static final String workerMsg = "worker";
    // invalid argument
    protected static final String invalidArgMsg = "Invalid argument(s), please use the correct form: ";
    // answer
    protected static final String ansMsg = "ans";
    public static void argInstruct(String exeName, String[] args) {
        StringBuilder argNames = new StringBuilder();
        argNames.append(exeName);
        for (String s : args) {
            argNames.append(" ").append("<").append(s).append(">");
        }
        System.out.println(invalidArgMsg + argNames);
    }
    // input prompt
    protected static final String inputPrompt = """
            Pleaser enter your command:\s
            info - check the information of the available workers\s
            del <worker_IP> - remove a worker by specifying its IP\s
            crack <MD5> - submit a password's MD5 code for the workers to crack\s
            exit - quit the program
            """;
    // white space
    protected static final String whiteSpace = "\\s+";
    // alphabet
    protected static final char[] ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };
    // find the index of a given character in the alphabet
    public static int getIndex (char ch) {
        if (('A' <= ch) && (ch <= 'Z')) {return (int) ch - 65;}
        else if (('a' <= ch) && (ch <= 'z')) {return (int) ch - 71;}
        else {return -1;}
    }
}
