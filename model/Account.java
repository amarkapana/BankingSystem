package model;

public class Account {
    private int id;
    private String username;
    private String pin;  // Changed from password to pin for consistency
    private double balance;

    public Account(int id, String username, String pin, double balance) {
        this.id = id;
        this.username = username;
        this.pin = pin;
        this.balance = balance;
    }

    public Account(String username, String password) {
        this.username = username;
        this.pin = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
