

import dao.UserDAO;
import db.DBConnection;
import java.sql.*;

public class AccountLockService {
    private static final int MAX_ATTEMPTS = 3;
    
    public static boolean isAccountLocked(String username) {
        String sql = "SELECT account_locked, lock_time FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getBoolean("account_locked")) {
                Timestamp lockTime = rs.getTimestamp("lock_time");
                // Check if 30 minutes have passed (for auto-unlock)
                if (lockTime != null && 
                    System.currentTimeMillis() - lockTime.getTime() > 30 * 60 * 1000) {
                    unlockUserAccount(username);
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
 * Unlocks a user account by resetting failed attempts and lock status
 * @param username The username of the account to unlock
 * @return true if unlock was successful, false otherwise
 */
public static boolean unlockUserAccount(String username) {
    // Validate input
    if (username == null || username.trim().isEmpty()) {
        System.err.println("[ERROR] Username cannot be null or empty");
        return false;
    }

    // SQL query to unlock account
    String sql = "UPDATE users SET account_locked = FALSE, failed_attempts = 0, lock_time = NULL WHERE username = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // Set parameters
        stmt.setString(1, username.trim());
        
        // Execute update
        int rowsUpdated = stmt.executeUpdate();
        
        // Check if any rows were affected
        if (rowsUpdated > 0) {
            System.out.println("[SUCCESS] Unlocked account for: " + username);
            return true;
        } else {
            System.out.println("[WARNING] No account found with username: " + username);
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("[ERROR] Failed to unlock account for " + username + ": " + e.getMessage());
        return false;
    }
}

    public static void recordFailedAttempt(String username) {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            checkAndLockAccount(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void checkAndLockAccount(String username) throws SQLException {
        String sql = "SELECT failed_attempts FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt("failed_attempts") >= MAX_ATTEMPTS) {
                lockAccount(username);
            }
        }
    }
    
    private static void lockAccount(String username) throws SQLException {
        String sql = "UPDATE users SET account_locked = TRUE, lock_time = NOW() WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
    
   public static boolean resetFailedAttempts(String username) {
    System.out.println("[DEBUG] Unlocking user: " + username.trim()); // Check for hidden spaces
    String sql = "UPDATE users SET failed_attempts = 0, account_locked = FALSE, lock_time = NULL WHERE username = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username.trim()); // Trim whitespace
        int rowsUpdated = stmt.executeUpdate();
        System.out.println("[DEBUG] Rows updated: " + rowsUpdated); // Should print 1
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.err.println("[ERROR] Unlock failed: " + e.getMessage()); // Log detailed error
        return false;
    }
}
    
    public static int getFailedAttempts(String username) {
        String sql = "SELECT failed_attempts FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("failed_attempts") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}