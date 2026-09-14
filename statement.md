# Project Statement

## Project Title

Banking Management System

## Problem Statement

Banking operations involve several interconnected activities such as account creation, customer management, deposits, withdrawals, fund transfers, transaction tracking, and administrative monitoring.

Managing these operations in separate or unstructured systems can make the process difficult to maintain and monitor. The objective of this project is to develop a modular Java-based Banking Management System that integrates these core banking operations into a single console application.

The system provides customer and administrator workflows while incorporating authentication, input validation, transaction management, persistent storage, reporting, and audit logging.

## Scope of the Project

The project covers the development of a console-based banking application with:

- Savings and Current account management.
- Customer authentication using account number and PIN.
- Administrator authentication.
- Account creation and closure.
- Deposits and withdrawals.
- Account-to-account fund transfers.
- Current Account overdraft support.
- Beneficiary management.
- Transaction history and filtering.
- Account statements.
- Customer profile and PIN management.
- Interest calculation for Savings Accounts.
- Account freeze and unfreeze functionality.
- Customer and administrator reports.
- Persistent storage using Java serialization.
- Audit logging and input validation.

The project is intended as an academic banking simulation and is not designed for deployment as a production banking system.

## Target Users

### Customers

Customers can:

- Create and manage accounts.
- Perform deposits and withdrawals.
- Transfer funds.
- Manage beneficiaries.
- View balances and transactions.
- Generate statements and reports.
- Manage their profile and PIN.

### Bank Administrators

Administrators can:

- Monitor customer accounts.
- Search and view account information.
- View transactions.
- Freeze or unfreeze accounts.
- Close accounts.
- View bank statistics and reports.
- Monitor audit logs.

## High-Level Features

1. **Account Management**  
   Creation, viewing, management, and closure of Savings and Current Accounts.

2. **Authentication**  
   Customer PIN-based login and administrator authentication with login-attempt control.

3. **Transaction Management**  
   Deposits, withdrawals, balance inquiry, and transaction history.

4. **Fund Transfer**  
   Account-to-account transfers with transfer limits, validation, and rollback handling.

5. **Beneficiary Management**  
   Add, view, remove, and transfer to saved beneficiaries.

6. **Profile Management**  
   Update customer details and change PIN securely.

7. **Statements & Reports**  
   Account statements, transaction filtering, monthly summaries, and banking statistics.

8. **Account Controls**  
   Account freezing, unfreezing, and closure.

9. **Persistence**  
   Account data is stored using Java serialization for persistence between executions.

10. **Audit Logging**  
    Important system events are recorded for administrative monitoring.

## Technologies Used

- Java
- Object-Oriented Programming
- Java Collections Framework
- Java Serialization
- Java File I/O
- Java Time API
- Git
- GitHub
- Visual Studio Code

## Project Architecture

The system follows a modular package-based architecture:

```text
com.banking
├── admin
├── cli
├── exception
├── model
├── service
└── validation