import java.sql.*;
import java.util.*;

public class TransactionDAO {
    public void addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, t.getAccountId());
            stmt.setString(2, t.getType());
            stmt.setDouble(3, t.getAmount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] Add transaction failed: " + e.getMessage());
        }
    }

    public List<Transaction> getLast5(int accountId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY timestamp DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("id"),
                    rs.getInt("account_id"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Fetch transactions failed: " + e.getMessage());
        }
        return list;
    }
}