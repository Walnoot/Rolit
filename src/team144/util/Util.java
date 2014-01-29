package team144.util;

public class Util {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] LEGAL_CHARACTERS = ALPHABET
            .concat(ALPHABET.toLowerCase()).concat("0123456789_").toCharArray();
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 16;
    
    public static void main(String[] args) {
        System.out.println(isValidName("Walnoot."));
    }
    
    /**
     * Creates a String containing all elements of an array, separated by spaces
     * 
     * @return aforementioned String
     */
    public static String concat(String[] strings) {
        if (strings.length == 0) return "";
        StringBuilder sb = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            sb.append(" ");
            sb.append(strings[i]);
        }
        return sb.toString();
    }
    
    public static boolean isValidName(String name) {
        if (name.length() < MIN_NAME_LENGTH) return false;
        if (name.length() > MAX_NAME_LENGTH) return false;
        
        for (int i = 0; i < name.length(); i++) {
            if (!contains(name.charAt(i), LEGAL_CHARACTERS)) return false;
        }
        
        return true;
    }
    
    private static boolean contains(char testChar, char[] testValues) {
        boolean contains = false;
        
        for (int i = 0; i < testValues.length; i++) {
            if (testValues[i] == testChar) contains = true;
        }
        
        return contains;
    }
}
