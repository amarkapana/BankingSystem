import java.nio.file.*;
import java.sql.*;

import db.DBConnection;

public class SchemaLoader {
    public static void main(String[] args) {
        try {
            // Load SQL file as String
           String sql = Files.readString(Paths.get("schema.sql")); // adjust path if needed

            // Split statements by semicolon
            String[] statements = sql.split(";");

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }

                System.out.println("✅ Database & tables created successfully.");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to load schema: " + e.getMessage());
        }
    }
}
