package dao;
import db.DBConnection;
import model.Transaction;

import java.sql.*;
import java.util.List;

public class TransactionDAO {
    public static String getMiniStatement(String username) {
        StringBuilder sb = new StringBuilder();
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM transactions WHERE username=? ORDER BY date DESC LIMIT 5")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                sb.append(rs.getString("date"))
                  .append(" - ")
                  .append(rs.getString("type"))
                  .append(" - ")
                  .append(rs.getDouble("amount"))
                  .append("\n");
            }
        } catch(Exception e) {
            e.printStackTrace();
            sb.append("Error fetching mini statement.");
        }
        return sb.toString();
    }

    public List<Transaction> getLast5(int accountId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLast5'");
    }

    public void addTransaction(Transaction transaction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addTransaction'");
    }
}
