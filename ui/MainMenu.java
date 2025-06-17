import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import db.DBConnection;
import model.Account;

public class MainMenu extends JFrame {
    private Account account;
    private JLabel balanceLabel;

    public MainMenu(Account account) {
        this.account = account;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ATM Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + account.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel("Balance: $" + account.getBalance());
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        headerPanel.add(balanceLabel, BorderLayout.EAST);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(Color.WHITE);

        // Add vertical spacing
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createATMButton("Check Balance", e -> checkBalance()));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createATMButton("Deposit", e -> deposit()));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createATMButton("Withdraw", e -> withdraw()));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createATMButton("Mini Statement", e -> miniStatement()));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createATMButton("Logout", e -> logout()));

        // Add admin button if applicable
        if (isAdmin(account.getUsername())) {
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(createATMButton("Admin Dashboard", e -> {
                this.dispose();
                new AdminGUI().setVisible(true);
            }));
        }

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createATMButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }

    private boolean isAdmin(String username) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT is_admin FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("is_admin");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkBalance() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT balance FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, account.getUsername());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                account.setBalance(balance);
                balanceLabel.setText("Balance: $" + balance);
                JOptionPane.showMessageDialog(this, 
                    "Your current balance: $" + balance,
                    "Balance", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            showError("Error checking balance: " + ex.getMessage());
        }
    }

    private void deposit() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter deposit amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                showError("Amount must be positive");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                // Update balance
                String updateSql = "UPDATE accounts SET balance = balance + ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDouble(1, amount);
                updateStmt.setString(2, account.getUsername());
                updateStmt.executeUpdate();

                // Record transaction
                String transSql = "INSERT INTO transactions (username, type, amount) VALUES (?, 'DEPOSIT', ?)";
                PreparedStatement transStmt = conn.prepareStatement(transSql);
                transStmt.setString(1, account.getUsername());
                transStmt.setDouble(2, amount);
                transStmt.executeUpdate();

                conn.commit();
                
                // Update local account balance
                account.setBalance(account.getBalance() + amount);
                balanceLabel.setText("Balance: $" + account.getBalance());
                JOptionPane.showMessageDialog(this, 
                    "Deposit successful!\nNew balance: $" + account.getBalance(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                showError("Deposit failed: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            showError("Invalid amount format");
        }
    }

    private void withdraw() {
        String amtStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                showError("Amount must be positive");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                // Check balance with locking
                String balanceSql = "SELECT balance FROM accounts WHERE username = ? FOR UPDATE";
                PreparedStatement balanceStmt = conn.prepareStatement(balanceSql);
                balanceStmt.setString(1, account.getUsername());
                ResultSet rs = balanceStmt.executeQuery();
                
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (currentBalance >= amount) {
                        // Update balance
                        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE username = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDouble(1, amount);
                        updateStmt.setString(2, account.getUsername());
                        updateStmt.executeUpdate();

                        // Record transaction
                        String transSql = "INSERT INTO transactions (username, type, amount) VALUES (?, 'WITHDRAW', ?)";
                        PreparedStatement transStmt = conn.prepareStatement(transSql);
                        transStmt.setString(1, account.getUsername());
                        transStmt.setDouble(2, amount);
                        transStmt.executeUpdate();

                        conn.commit();
                        
                        // Update local account balance
                        account.setBalance(currentBalance - amount);
                        balanceLabel.setText("Balance: $" + account.getBalance());
                        JOptionPane.showMessageDialog(this, 
                            "Withdrawal successful!\nNew balance: $" + account.getBalance(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        conn.rollback();
                        showError("Insufficient funds");
                    }
                }
            } catch (SQLException ex) {
                showError("Withdrawal failed: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            showError("Invalid amount format");
        }
    }

    private void miniStatement() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT type, amount, timestamp FROM transactions " +
                         "WHERE username = ? ORDER BY timestamp DESC LIMIT 5";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, account.getUsername());
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder statement = new StringBuilder();
            statement.append("=== Mini Statement ===\n");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            while (rs.next()) {
                statement.append(String.format("%-10s %10.2f  %s%n",
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    dateFormat.format(rs.getTimestamp("timestamp"))));
            }
            
            JTextArea textArea = new JTextArea(statement.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                "Mini Statement", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError("Error retrieving statement: " + ex.getMessage());
        }
    }

    private void logout() {
        this.dispose();
        new ATM_GUI().setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}