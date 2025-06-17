# 💳 Banking System (ATM + Admin Dashboard)

A complete desktop-based Java banking system with GUI, supporting customer and admin roles. This project demonstrates real-world banking operations like login, account handling, transaction history, and admin controls using Java Swing and MySQL.

---

## 📁 Project Structure

BankingSystem/

├── dao/ # Data access layer (UserDAO, AccountDAO, TransactionDAO)

├── model/ # Java models (User, Account, Transaction)

├── service/ # Business logic (AccountLockService, BankingService)

├── ui/ # User Interface (ATM_GUI, AdminGUI, MainMenu)

├── util/ # Utilities (SecurityUtil, AuditLogger, etc.)

├── db/ # DBConnection.java (MySQL config)

├── schema.sql # SQL script to create tables

├── audit.log # Activity log file

└── lib/ # MySQL JDBC driver


## ✅ Features

### 🔐 User Functionality
- User Registration with PIN
- Login with hashed PIN verification
- Account Locking after 3 failed login attempts
- Deposit and Withdraw operations
- Balance check
- Mini Statement (Transaction history)

### 🛠 Admin Dashboard
- Admin Login
- View all users & accounts
- Unlock user accounts
- View transaction history of users

### 🗃 Technical Highlights
- Java Swing GUI
- JDBC with MySQL
- DAO pattern for data access
- BCrypt hashing for PIN security
- Audit Logging
- Clean modular code (MVC-style)

---

## 🛠 Setup Instructions

### Prerequisites
- Java JDK 8 or later
- MySQL Server
- Any Java IDE (e.g., IntelliJ, Eclipse, VS Code)

### Database Setup
1. Create a new MySQL database:
    ```sql
    CREATE DATABASE banking_system;
    ```

2. Execute the provided `schema.sql` in your MySQL DB.

3. Update `DBConnection.java` with your DB credentials:
    ```java
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    ```

### Run the Project
- Open `Main.java` or `ATM_GUI.java` to start the application.
- Use `SchemaLoader.java` (if included) to validate schema setup.

---

## 📸 Screenshots

> ATM Login GUI
> ![image](https://github.com/user-attachments/assets/03cade6c-c927-4a13-91e8-1281e30f6b7d)

> ![image](https://github.com/user-attachments/assets/3b8f393d-a030-4109-b5b2-cf3a66b616e5)
> ![image](https://github.com/user-attachments/assets/28a58072-1a52-407a-bba8-0a458d84f056)
> ![image](https://github.com/user-attachments/assets/1e8b44d8-9f39-47fd-8660-00b9c686a4b0)
> ![image](https://github.com/user-attachments/assets/9b433006-6ee6-494b-a968-4235e1c71086)
> ![image](https://github.com/user-attachments/assets/c1d64692-495b-4360-8dda-6faae57b8f69)
> ![image](https://github.com/user-attachments/assets/62098b35-c4c1-42db-b0b3-1e23e28f8c7e)
> ![image](https://github.com/user-attachments/assets/5827439a-a915-43af-b7ce-a20bd744106f)





> Admin Dashboard  
>![image](https://github.com/user-attachments/assets/176038fa-d82e-439a-b703-cfa7fa3072df)


---

## 📌 Notes

- Default admin credentials (if set manually) should be inserted via database.
- Transactions are logged, and user attempts are monitored for lockout.

---

## 🧑‍💻 Author

Amarnath Kapanapalle
Email: amarkapana@gmail.com 

---

## 📜 License

This project is licensed for academic use. Customize it for learning or evaluation purposes only.
