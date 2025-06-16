package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

public class AuditLogger {
    public static void logUnlock(String adminUser, String targetUser) {
        String path = new File("audit.log").getAbsolutePath();
    System.out.println("[DEBUG] Audit log path: " + path);
        String log = String.format("[%s] %s unlocked %s", 
            new Date(0), adminUser, targetUser);
        
        // Log to file
        try (FileWriter fw = new FileWriter("audit.log", true)) {
            fw.write(log + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
        }
        
        // Also print to console for debugging
        System.out.println("[AUDIT] " + log);
    }
    // TEMPORARY TEST HARNESS (remove after testing)

}
