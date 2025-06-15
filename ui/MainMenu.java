import java.util.*;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BankingService bankingService = new BankingService();
    private Account loggedInUser = null;

    public void run() {
        while (true) {
            if (loggedInUser == null) showWelcomeMenu();
            else showAccountMenu();
        }
    }

    private void showWelcomeMenu() {
        System.out.println("\n--- Welcome to Banking App ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
        switch (scanner.nextLine()) {
            case "1" -> register();
            case "2" -> login();
            case "3" -> System.exit(0);
            default -> System.out.println("Invalid choice.");
        }
    }

    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        boolean success = bankingService.register(username, password);
        System.out.println(success ? "Registration successful." : "Registration failed.");
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        loggedInUser = bankingService.login(username, password);
        if (loggedInUser == null) {
            System.out.println("Login failed. Try again.");
        } else {
            System.out.println("Login successful. Welcome " + loggedInUser.getUsername() + "!");
        }
    }

    private void showAccountMenu() {
        System.out.println("\n--- Account Menu ---");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Mini Statement");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");
        switch (scanner.nextLine()) {
            case "1" -> System.out.println("Balance: ₹" + loggedInUser.getBalance());
            case "2" -> deposit();
            case "3" -> withdraw();
            case "4" -> bankingService.printMiniStatement(loggedInUser.getId());
            case "5" -> loggedInUser = null;
            default -> System.out.println("Invalid choice.");
        }
    }

    private void deposit() {
        System.out.print("Enter amount to deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());
        loggedInUser = bankingService.deposit(loggedInUser, amount);
    }

    private void withdraw() {
        System.out.print("Enter amount to withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());
        loggedInUser = bankingService.withdraw(loggedInUser, amount);
    }
}