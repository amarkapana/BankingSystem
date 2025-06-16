CREATE DATABASE IF NOT EXISTS banking_app;
USE banking_app;

-- Users table stores login credentials
-- In your schema.sql file, modify the users table:
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  pin VARCHAR(255) NOT NULL,
  -- Add these new columns:
  account_locked BOOLEAN DEFAULT FALSE,
  lock_time TIMESTAMP NULL,
  failed_attempts INT DEFAULT 0
);

-- Accounts table stores financial info (no pin column needed)
CREATE TABLE IF NOT EXISTS accounts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  FOREIGN KEY (username) REFERENCES users(username)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  type ENUM('DEPOSIT', 'WITHDRAW') NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username)
);
