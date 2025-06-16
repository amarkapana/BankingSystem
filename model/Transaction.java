package model;
public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private double amount;
    private String timestamp;

    public Transaction(int accountId, String type, double amount) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
    }

    public Transaction(int id, int accountId, String type, double amount, String timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }
}