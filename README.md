# Banking Management System

A console-based Banking Management System developed in Java using Object-Oriented Programming, modular architecture, file-based persistence, validation, authentication, transaction management, reporting, and audit logging.

The project is designed as an intermediate-level Java application demonstrating how core Java and OOP concepts can be combined to build a structured real-world banking application.

---

## Project Overview

The Banking Management System provides separate interfaces for customers and administrators.

Customers can create and manage their bank accounts, perform transactions, transfer money, manage beneficiaries, view statements, manage their profiles, and generate reports.

Administrators can monitor accounts, manage account status, view transactions, access bank statistics and reports, freeze/unfreeze accounts, close accounts, and inspect audit logs.

The system stores account data using Java serialization so that account information can persist between program executions.

---

## Problem Statement

Traditional banking operations involve multiple activities such as account management, deposits, withdrawals, transfers, transaction tracking, customer profile management, and administrative monitoring.

The objective of this project is to develop a simple but structured banking application that integrates these operations into one system while applying software engineering principles such as modularity, validation, authentication, exception handling, persistence, and audit logging.

---

## Objectives

- Develop a functional console-based banking application.
- Apply Object-Oriented Programming concepts in Java.
- Implement different types of bank accounts.
- Provide secure customer and administrator authentication.
- Support common banking transactions.
- Maintain transaction history.
- Implement account and customer management.
- Provide beneficiary management.
- Generate account statements and reports.
- Provide administrative monitoring and controls.
- Maintain persistent account data.
- Implement input validation and exception handling.
- Maintain an audit log for important system events.
- Organize the project using packages and modular classes.

---

## Main Features

### 1. Account Management

- Create a new bank account.
- Support Savings Account.
- Support Current Account.
- Generate unique account numbers.
- Store customer information.
- View account details.
- Close accounts.
- Maintain account status.

### 2. Customer Authentication

- Customer login using account number and PIN.
- Maximum login attempt control.
- Validation of account status.
- Frozen accounts cannot access normal banking operations.

### 3. Deposits

Customers can deposit money into their accounts.

The system validates:

- Amount format.
- Positive transaction amount.
- Account status.

Successful deposits are recorded in transaction history.

### 4. Withdrawals

Customers can withdraw money from their accounts.

Savings Accounts enforce available-balance restrictions.

Current Accounts support an overdraft facility subject to the configured overdraft limit.

### 5. Money Transfers

Customers can transfer money between accounts.

The system includes:

- Account existence validation.
- Self-transfer prevention.
- Active-account validation.
- Frozen receiver protection.
- Per-transfer limit.
- Daily outgoing transfer limit.
- Balance/overdraft validation.
- Sender transaction recording.
- Receiver transaction recording.
- Rollback handling if the receiving operation fails.

### 6. Beneficiary Management

Customers can maintain saved beneficiaries.

Operations include:

- View beneficiaries.
- Add beneficiary.
- Remove beneficiary.
- Transfer to a saved beneficiary.

Duplicate beneficiaries are prevented.

### 7. Transaction History

The system records transactions with:

- Transaction ID.
- Transaction type.
- Amount.
- Description.
- Date and time.

Customers can filter transactions by categories such as:

- All Transactions
- Deposits
- Withdrawals
- Transfers Sent
- Transfers Received
- Account Opening
- PIN Changes

### 8. Account Statements

Customers can generate account statements containing:

- Customer information.
- Account information.
- Account type.
- Account status.
- Current balance.
- Deposit totals.
- Withdrawal totals.
- Transfer totals.
- Transaction details.

### 9. Profile Management

Customers can view and update:

- Name
- Phone number
- Email
- Address

Customers can also change their PIN after verifying the existing PIN.

### 10. Interest Calculation

Savings Accounts support interest calculation based on the configured interest rate.

Current Accounts do not provide savings-style interest calculation.

### 11. Current Account Overdraft

Current Accounts support an overdraft facility.

The system prevents withdrawals beyond the configured overdraft limit.

### 12. Admin Management

Administrators can:

- View all accounts.
- Search for accounts.
- View account details.
- View account transactions.
- Close accounts.
- Freeze accounts.
- Unfreeze accounts.
- View bank dashboard information.
- View account statistics.
- Generate reports.
- View audit logs.

### 13. Reports

The system provides reporting features including:

#### Bank Reports

- Bank summary.
- Transaction statistics.
- Monthly account summary.
- Account activity by date.
- Account activity by month.

#### Customer Reports

- Monthly transaction summary.
- Transactions by date.
- Transactions by month.
- Transaction type totals.

### 14. Audit Logging

Important system activities are recorded in:

PROJECT REQUIREMENTS
====================

1. Java Development Kit (JDK) 17 or higher
2. Java compiler (javac)
3. Java runtime (java)
4. Git
5. GitHub account/repository
6. Visual Studio Code or any Java-compatible IDE
7. Windows PowerShell or Command Prompt
8. Minimum 4 GB RAM recommended
9. Approximately 50 MB of available disk space

JAVA VERSION
============

The project is developed using standard Java features and
does not require external Java libraries or third-party
dependencies.

BUILD
=====

Windows:
    .\build.bat

Run:
    java -cp out com.banking.cli.Main

STORAGE
=======

The application creates/uses the following runtime files:

    data/accounts.dat
    data/audit.log

These files are generated during application execution and
are excluded from Git version control.

EXTERNAL DEPENDENCIES
=====================

None.

All application functionality is implemented using the
standard Java library.

## 📁 Project File Structure

```text
BankingManagementSystem/
│
├── src/
│   └── com/
│       └── banking/
│           │
│           ├── admin/
│           │   └── Admin.java
│           │
│           ├── cli/
│           │   └── Main.java
│           │
│           ├── model/
│           │   ├── Account.java
│           │   ├── SavingsAccount.java
│           │   ├── CurrentAccount.java
│           │   ├── Customer.java
│           │   ├── Transaction.java
│           │   └── Beneficiary.java
│           │
│           ├── service/
│           │   ├── Bank.java
│           │   ├── AuthenticationService.java
│           │   ├── StatementService.java
│           │   ├── ReportService.java
│           │   └── AuditLogger.java
│           │
│           ├── validation/
│           │   └── InputValidator.java
│           │
│           └── exception/
│               └── BankingException.java
│
├── data/
│   ├── accounts.dat
│   └── audit.log
│
├── out/
│
├── build.bat
├── build.sh
├── README.md
├── statement.md
└── requirements.txt
