public class Account {
    private int id;
    private String username;
    private String password;
    private double balance;

    public Account(int id, String username, String password, double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}