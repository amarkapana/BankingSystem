import java.util.List;

public class BankingService {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public boolean register(String username, String password) {
        return accountDAO.register(new Account(username, password));
    }

    public Account login(String username, String password) {
        return accountDAO.login(username, password);
    }

    public Account deposit(Account user, double amount) {
        double newBalance = user.getBalance() + amount;
        if (accountDAO.updateBalance(user.getId(), newBalance)) {
            transactionDAO.addTransaction(new Transaction(user.getId(), "Deposit", amount));
            user.setBalance(newBalance);
        }
        return user;
    }

    public Account withdraw(Account user, double amount) {
        if (user.getBalance() >= amount) {
            double newBalance = user.getBalance() - amount;
            if (accountDAO.updateBalance(user.getId(), newBalance)) {
                transactionDAO.addTransaction(new Transaction(user.getId(), "Withdraw", amount));
                user.setBalance(newBalance);
            }
        } else {
            System.out.println("Insufficient funds.");
        }
        return user;
    }

    public void printMiniStatement(int accountId) {
        List<Transaction> transactions = transactionDAO.getLast5(accountId);
        System.out.println("\n--- Mini Statement ---");
        for (Transaction t : transactions) {
            System.out.println(t.getTimestamp() + " | " + t.getType() + " | ₹" + t.getAmount());
        }
    }
}