package dao;

import java.sql.*;
import db.DBConnection;
import util.SecurityUtil;

public class UserDAO {
    public boolean isUsernameTaken(String username) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume taken if error occurs
        }
    }
    
    public boolean registerUser(String username, String pin) {
        String hashedPin = SecurityUtil.hashPin(pin);
        
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            
            try {
                // Insert into users table
                PreparedStatement userStmt = con.prepareStatement(
                    "INSERT INTO users (username, pin) VALUES (?, ?)");
                userStmt.setString(1, username);
                userStmt.setString(2, hashedPin);
                userStmt.executeUpdate();
                
                // Create associated account
                PreparedStatement accountStmt = con.prepareStatement(
                    "INSERT INTO accounts (username, balance) VALUES (?, 0.00)");
                accountStmt.setString(1, username);
                accountStmt.executeUpdate();
                
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate(String username, String pin) {
        String sql = "SELECT pin FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("pin");
                return SecurityUtil.verifyPin(pin, storedHash);
            }
            return false;
        } catch (SQLException e) {
            System.out.println("[ERROR] Authentication failed: " + e.getMessage());
            return false;
        }
    }
}