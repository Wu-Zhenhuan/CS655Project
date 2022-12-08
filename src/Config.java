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
    // input prompt
    protected static final String inputPrompt = "Input command: ";
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
