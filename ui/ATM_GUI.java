import javax.imageio.ImageIO;
import javax.swing.*;

import dao.AccountDAO;
import dao.UserDAO;
import db.DBConnection;
import model.Account;
import util.SecurityUtil;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.awt.image.BufferedImage;

public class ATM_GUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField pinField;
    private JButton loginButton, registerButton;
    private BufferedImage atmImage;

    public ATM_GUI() {
        setTitle("ATM Machine");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Load ATM background
        try {
            atmImage = ImageIO.read(new File("atm_placeholder.jpg"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load ATM image");
            System.exit(1);
        }

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        // Background panel with image
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(atmImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(bgPanel, JLayeredPane.DEFAULT_LAYER);

        // Login components (position over image)
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false); // Transparent
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        loginPanel.setBounds(180, 180, 300, 150); // Adjust position as needed

        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        loginPanel.add(pinField);

        loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        registerButton = new JButton("Register");
        loginPanel.add(registerButton);

        layeredPane.add(loginPanel, JLayeredPane.PALETTE_LAYER);
        add(layeredPane);

        // Button actions (use your existing methods)
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
    }
    private void login() {
    String username = usernameField.getText().trim();
    String pin = new String(pinField.getPassword()).trim();

    if (username.isEmpty() || pin.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter both username and PIN");
        return;
    }

    // Check if account is locked first
    if (AccountLockService.isAccountLocked(username)) {
        JOptionPane.showMessageDialog(this,
            "Account locked due to too many failed attempts.\nPlease contact admin.",
            "Account Locked", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection conn = DBConnection.getConnection()) {
        // Get stored user data
        String sql = "SELECT id, username, pin FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String storedPinHash = rs.getString("pin");
            
            if (SecurityUtil.verifyPin(pin, storedPinHash)) {
                // Successful login - reset failed attempts
                AccountLockService.resetFailedAttempts(username);
                
                // Get account details (without PIN)
                Account account = new AccountDAO().getAccountByUsername(username);
                
                if (account != null) {
                    // Open main menu
                    this.dispose();
                    new MainMenu(account).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Account information not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Failed login
                AccountLockService.recordFailedAttempt(username);
                int attemptsLeft = 3 - AccountLockService.getFailedAttempts(username);
                
                JOptionPane.showMessageDialog(this,
                    "Invalid PIN. Attempts left: " + attemptsLeft,
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username not found");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Database error: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    // In ATM_GUI.java or your login service
private void handleFailedLogin(String username) {
    try (Connection conn = DBConnection.getConnection()) {
        // 1. Increment failed attempts
        String updateSql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(updateSql);
        stmt.setString(1, username);
        stmt.executeUpdate();
        
        // 2. Check if should be locked
        String checkSql = "SELECT failed_attempts FROM users WHERE username = ?";
        stmt = conn.prepareStatement(checkSql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next() && rs.getInt("failed_attempts") >= 3) {
            // 3. Lock the account
            String lockSql = "UPDATE users SET account_locked = TRUE, lock_time = NOW() WHERE username = ?";
            stmt = conn.prepareStatement(lockSql);
            stmt.setString(1, username);
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "Account locked due to too many failed attempts",
                "Security Alert", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    private void register() {
    String username = usernameField.getText().trim();
    String pin = new String(pinField.getPassword()).trim();

    if (username.isEmpty() || pin.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter both username and PIN");
        return;
    }

    if (pin.length() < 4) {
        JOptionPane.showMessageDialog(this, "PIN must be at least 4 digits");
        return;
    }

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false); // Start transaction

        try {
            // Check if username exists
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists");
                return;
            }

            // Hash the PIN before storing
            String pinHash = SecurityUtil.hashPin(pin);

            // Insert new user (only in users table)
            String userSql = "INSERT INTO users (username, pin) VALUES (?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, username);
            userStmt.setString(2, pinHash);
            userStmt.executeUpdate();

            // Create account with zero balance
            String accountSql = "INSERT INTO accounts (username, balance) VALUES (?, 0.00)";
            PreparedStatement accountStmt = conn.prepareStatement(accountSql);
            accountStmt.setString(1, username);
            accountStmt.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            usernameField.setText("");
            pinField.setText("");
        } catch (SQLException ex) {
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
        ex.printStackTrace();
    }
}
    public static void main(String[] args) {
        // Verify database tables exist
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "users", null);
            if (!tables.next()) {
                JOptionPane.showMessageDialog(null, 
                    "Database tables not found. Please run SchemaLoader first.");
                System.exit(1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Failed to connect to database: " + ex.getMessage());
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            ATM_GUI atm = new ATM_GUI();
            atm.setVisible(true);
        });
    }
}