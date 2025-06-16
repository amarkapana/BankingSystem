package util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SecurityUtil {
    // PIN validation with strength requirements
    public static boolean isPinValid(String pin) {
        // Requires 4-6 digits, no repeating sequences, no simple patterns
        return pin.matches("^(?!.*(.)\\1{2})(?!0123|1234|2345|3456|4567|5678|6789|9876|8765|7654|6543|5432|4321|3210)\\d{4,6}$");
    }

    public static String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(pin.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static boolean verifyPin(String inputPin, String storedHash) {
        return hashPin(inputPin).equals(storedHash);
    }
}