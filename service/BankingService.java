

import java.util.List;
import dao.AccountDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import model.Account;
import model.Transaction;

public class BankingService {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final UserDAO userDAO = new UserDAO();

    public boolean register(String username, String pin) {
        if (userDAO.isUsernameTaken(username)) {
            System.out.println("Username already taken");
            return false;
        }
        return userDAO.registerUser(username, pin);
    }

    public Account login(String username, String pin) {
        if (!userDAO.authenticate(username, pin)) {
            System.out.println("Invalid credentials");
            return null;
        }
        return accountDAO.getAccountByUsername(username);
    }

    public Account deposit(Account user, double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive");
            return user;
        }
        
        double newBalance = user.getBalance() + amount;
        if (accountDAO.updateBalance(user.getId(), newBalance)) {
            transactionDAO.addTransaction(new Transaction(user.getId(), "DEPOSIT", amount));
            user.setBalance(newBalance);
        }
        return user;
    }

    public Account withdraw(Account user, double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive");
            return user;
        }
        
        if (user.getBalance() >= amount) {
            double newBalance = user.getBalance() - amount;
            if (accountDAO.updateBalance(user.getId(), newBalance)) {
                transactionDAO.addTransaction(new Transaction(user.getId(), "WITHDRAW", amount));
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