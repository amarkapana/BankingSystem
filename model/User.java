package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String pin;
    private boolean isAdmin;
    private boolean accountLocked;
    private Timestamp lockTime;
    private int failedAttempts;

    // Constructor
    public User(int id, String username, String pin, boolean isAdmin, 
                boolean accountLocked, Timestamp lockTime, int failedAttempts) {
        this.id = id;
        this.username = username;
        this.pin = pin;
        this.isAdmin = isAdmin;
        this.accountLocked = accountLocked;
        this.lockTime = lockTime;
        this.failedAttempts = failedAttempts;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public boolean isAdmin() { return isAdmin; }
    public boolean isAccountLocked() { return accountLocked; }
    public Timestamp getLockTime() { return lockTime; }
    public int getFailedAttempts() { return failedAttempts; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPin(String pin) { this.pin = pin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
    public void setAccountLocked(boolean locked) { accountLocked = locked; }
    public void setLockTime(Timestamp lockTime) { this.lockTime = lockTime; }
    public void setFailedAttempts(int attempts) { failedAttempts = attempts; }
}