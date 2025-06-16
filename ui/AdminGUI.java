
import dao.AdminDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminGUI extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshBtn, unlockBtn;

    public AdminGUI() {
        setTitle("Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        // Table setup
        String[] columnNames = {"Username", "Admin", "Lock Status", "Lock Time", "Failed Attempts"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        refreshBtn = new JButton("Refresh");
        unlockBtn = new JButton("Unlock Selected");
        
        // Style buttons
        styleButton(refreshBtn);
        styleButton(unlockBtn);
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(unlockBtn);

        // Add components
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        refreshBtn.addActionListener(e -> refreshUserTable());
        // Add debug to button listener
unlockBtn.addActionListener(e -> {
    System.out.println("[DEBUG] Unlock button clicked - " + new Date());
    unlockSelectedUser();
});
        searchField.addActionListener(e -> searchUsers());

        // Initial load
        refreshUserTable();
        
        // Center window
        setLocationRelativeTo(null);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
    }

    private void refreshUserTable() {
    new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
            // Background thread: Load data
            List<User> users = AdminDAO.getAllUsers();
            tableModel.setRowCount(0);
             for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUsername(),
                user.isAdmin() ? "Yes" : "No",
                user.isAccountLocked() ? "Locked" : "Active",
                formatLockTime(user.getLockTime()),
                user.getFailedAttempts()
            });
        }
            return null;
        }

        @Override
        protected void done() {
            // EDT thread: Update UI
            userTable.revalidate();
            userTable.repaint();
        }
    }.execute();
}

    private String formatLockTime(Timestamp lockTime) {
        return lockTime == null ? "Never" : 
               new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lockTime);
    }

    private void searchUsers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        
        List<User> users = AdminDAO.getAllUsers();
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(searchTerm)) {
                tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.isAdmin() ? "Yes" : "No",
                    user.isAccountLocked() ? "Locked" : "Active",
                    formatLockTime(user.getLockTime()),
                    user.getFailedAttempts()
                });
            }
        }
    }

    private void unlockSelectedUser() {
        if (userTable == null || tableModel == null) {
        JOptionPane.showMessageDialog(null, "UI not initialized", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int selectedRow = userTable.getSelectedRow();
    if (selectedRow < 0 || selectedRow >= tableModel.getRowCount()) {
        JOptionPane.showMessageDialog(null, "Invalid selection", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // String username = (String) tableModel.getValueAt(selectedRow, 0);
    // if (username == null || username.trim().isEmpty()) {
    //     JOptionPane.showMessageDialog(null, "Invalid username", "Error", JOptionPane.ERROR_MESSAGE);
    //     return;
    // }
    
    System.out.println("[DEBUG] Selected row: " + selectedRow);
    
    if (selectedRow >= 0) {
        String username = ((String) tableModel.getValueAt(selectedRow, 0)).trim();
        System.out.println("[DEBUG] Unlocking user: " + username);
        
        if (AdminDAO.unlockUserAccount(username)) {
            System.out.println("[SUCCESS] Unlocked: " + username);
            refreshUserTable(); // ← Will show in next file
        } else {
            System.err.println("[ERROR] Unlock failed for: " + username);
        }
    } else {
        System.out.println("[WARN] No row selected");
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }
}