package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            var hexString = new StringBuilder();
            for (byte element : hash) {
                String hex = Integer.toHexString(0xff & element);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if this string starts with specified number of zeros.
     * @param str the string to be checked.
     * @param numOfZeros the number of leading zeros.
     * @return true if str starts with specified number of zeros; false if str has less or more leading zeros.
     * @throws IllegalArgumentException if numOfZeros < 0 || numOfZeros > str.length || str.isEmpty.
     */
    public static boolean startsWithZeros(String str, int numOfZeros) {
        if (numOfZeros < 0 || numOfZeros > str.length() || str.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < numOfZeros; i++) {
            if (str.charAt(i) != '0') return false;
        }
        return str.charAt(numOfZeros) != '0';
    }
}
