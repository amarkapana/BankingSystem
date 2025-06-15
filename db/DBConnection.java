import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASS = "Amar@2004"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
