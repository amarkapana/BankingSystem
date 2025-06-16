package dao;

import model.User;
import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {
    public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT id, username, pin, is_admin, account_locked, lock_time, failed_attempts " +
                 "FROM users ORDER BY username";
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("pin"),
                rs.getBoolean("is_admin"),
                rs.getBoolean("account_locked"),
                rs.getTimestamp("lock_time"),
                rs.getInt("failed_attempts")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return users;
}
    public static boolean unlockUserAccount(String username) {
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction

        String sql = "UPDATE users SET account_locked=FALSE, failed_attempts=0, lock_time=NULL WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            int rowsUpdated = stmt.executeUpdate();
            conn.commit(); // Commit only if successful
            
            return rowsUpdated > 0;
        }
    } catch (SQLException e) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
        System.err.println("[DB ERROR] " + e.getMessage());
        return false;
    } finally {
        if (conn != null) try { conn.close(); } catch (SQLException e) {}
    }
}
}