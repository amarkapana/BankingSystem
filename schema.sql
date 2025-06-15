-- schema.sql

CREATE DATABASE IF NOT EXISTS banking_app;
USE banking_app;

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    balance DOUBLE DEFAULT 0.0
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    type VARCHAR(50),
    amount DOUBLE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Sample data
INSERT INTO accounts (username, password, balance)
VALUES 
('amar', 'amar@123', 5000.0),
('kiran', 'kiran@123', 10000.0);

INSERT INTO transactions (account_id, type, amount)
VALUES
(1, 'DEPOSIT', 2000),
(1, 'WITHDRAW', 1000),
(2, 'DEPOSIT', 500),
(2, 'WITHDRAW', 200);
