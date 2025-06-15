import java.sql.*;

public class AccountDAO {
    public boolean register(Account acc) {
        String sql = "INSERT INTO accounts (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, acc.getUsername());
            stmt.setString(2, SecurityUtil.hashPassword(acc.getPassword()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("[ERROR] Registration failed: " + e.getMessage());
            return false;
        }
    }

    public Account login(String username, String password) {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, SecurityUtil.hashPassword(password));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Login failed: " + e.getMessage());
        }
        return null;
    }

    public boolean updateBalance(int id, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("[ERROR] Balance update failed: " + e.getMessage());
            return false;
        }
    }

    public Account getById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Fetch account failed: " + e.getMessage());
        }
        return null;
    }
}
