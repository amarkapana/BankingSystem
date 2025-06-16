package dao;

import java.sql.*;
import db.DBConnection;
import model.Account;
import util.SecurityUtil;

public class AccountDAO {
    
    // Register should handle both users and accounts tables
    public boolean register(Account acc) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. First insert into users table
            String userSql = "INSERT INTO users (username, pin) VALUES (?, ?)";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, acc.getUsername());
                userStmt.setString(2, SecurityUtil.hashPin(acc.getPin()));
                
                int userRows = userStmt.executeUpdate();
                if (userRows != 1) {
                    conn.rollback();
                    return false;
                }
            }
            
            // 2. Then insert into accounts table
            String accountSql = "INSERT INTO accounts (username, balance) VALUES (?, ?)";
            try (PreparedStatement accountStmt = conn.prepareStatement(accountSql)) {
                accountStmt.setString(1, acc.getUsername());
                accountStmt.setDouble(2, acc.getBalance());
                
                int accountRows = accountStmt.executeUpdate();
                if (accountRows != 1) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            System.out.println("[ERROR] Registration failed: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
            }
        }
    }

    // Login should be handled by UserDAO, not AccountDAO
    // This method should only retrieve account info after successful authentication
    public Account getAccountByUsername(String username) {
        String sql = "SELECT a.id, a.username, a.balance FROM accounts a WHERE a.username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("id"),
                    rs.getString("username"),
                    null, // No PIN in Account object
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Fetch account failed: " + e.getMessage());
        }
        return null;
    }

    public boolean updateBalance(int id, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("[ERROR] Balance update failed: " + e.getMessage());
            return false;
        }
    }

    public Account getById(int id) {
        String sql = "SELECT a.id, a.username, a.balance FROM accounts a WHERE a.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("id"),
                    rs.getString("username"),
                    null, // No PIN in Account object
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Fetch account failed: " + e.getMessage());
        }
        return null;
    }
}