package com.banking.cli;

import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.SavingsAccount;
import com.banking.service.Bank;
import com.banking.service.StatementService;
import com.banking.validation.InputValidator;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static final Bank bank =
            new Bank();

    private static final StatementService statementService =
            new StatementService();

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            displayMainMenu();

            int choice = readInt(
                    "Enter your choice: "
            );

            switch (choice) {

                case 1:
                    createAccount();
                    break;

                case 2:
                    customerLogin();
                    break;

                case 3:
                    adminLogin();
                    break;

                case 4:
                    running = false;

                    System.out.println(
                            "\nThank you for using "
                                    + "Banking Management System."
                    );

                    break;

                default:
                    System.out.println(
                            "\nInvalid choice."
                    );
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {

        System.out.println(
                "\n======================================"
        );

        System.out.println(
                "       BANKING MANAGEMENT SYSTEM"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "1. Create Account"
        );

        System.out.println(
                "2. Customer Login"
        );

        System.out.println(
                "3. Admin Login"
        );

        System.out.println(
                "4. Exit"
        );

        System.out.println(
                "======================================"
        );
    }

    private static void createAccount() {

        System.out.println(
                "\n========== CREATE ACCOUNT =========="
        );

        String name;

        while (true) {

            System.out.print(
                    "Enter name: "
            );

            name =
                    scanner.nextLine().trim();

            if (InputValidator.isValidName(name)) {
                break;
            }

            System.out.println(
                    "Invalid name. Use letters and spaces only."
            );
        }

        String phone;

        while (true) {

            System.out.print(
                    "Enter 10-digit phone number: "
            );

            phone =
                    scanner.nextLine().trim();

            if (InputValidator.isValidPhone(phone)) {
                break;
            }

            System.out.println(
                    "Invalid phone number."
            );
        }

        String email;

        while (true) {

            System.out.print(
                    "Enter email: "
            );

            email =
                    scanner.nextLine().trim();

            if (InputValidator.isValidEmail(email)) {
                break;
            }

            System.out.println(
                    "Invalid email address."
            );
        }

        System.out.print(
                "Enter address: "
        );

        String address =
                scanner.nextLine().trim();

        String pin;

        while (true) {

            System.out.print(
                    "Create 4-digit PIN: "
            );

            pin =
                    scanner.nextLine().trim();

            if (InputValidator.isValidPin(pin)) {
                break;
            }

            System.out.println(
                    "PIN must contain exactly 4 digits."
            );
        }

        System.out.println(
                "\nSelect Account Type:"
        );

        System.out.println(
                "1. Savings Account"
        );

        System.out.println(
                "2. Current Account"
        );

        int typeChoice =
                readInt("Enter choice: ");

        String accountType;

        if (typeChoice == 1) {

            accountType = "savings";

        } else if (typeChoice == 2) {

            accountType = "current";

        } else {

            System.out.println(
                    "Invalid account type."
            );

            return;
        }

        double initialDeposit;

        while (true) {

            initialDeposit =
                    readDouble(
                            "Enter initial deposit: "
                    );

            if (InputValidator.isValidAmount(
                    initialDeposit)) {

                break;
            }

            System.out.println(
                    "Deposit must be greater than 0."
            );
        }

        Account account =
                bank.createAccount(
                        name,
                        phone,
                        email,
                        address,
                        pin,
                        accountType,
                        initialDeposit
                );

        if (account != null) {

            System.out.println(
                    "\nAccount created successfully!"
            );

            System.out.println(
                    "Customer ID    : "
                            + account.getCustomer()
                            .getCustomerId()
            );

            System.out.println(
                    "Account Number : "
                            + account.getAccountNumber()
            );

            System.out.println(
                    "Account Type   : "
                            + account.getAccountType()
            );

        } else {

            System.out.println(
                    "\nAccount creation failed."
            );
        }
    }

    private static void customerLogin() {

        System.out.println(
                "\n========== CUSTOMER LOGIN =========="
        );

        System.out.print(
                "Enter account number: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        Account account =
                bank.findAccount(accountNumber);

        if (account == null) {

            System.out.println(
                    "Account not found."
            );

            return;
        }

        if (!account.isActive()) {

            System.out.println(
                    "This account is closed."
            );

            return;
        }

        int attempts = 3;

        while (attempts > 0) {

            System.out.print(
                    "Enter PIN: "
            );

            String pin =
                    scanner.nextLine().trim();

            if (account.verifyPin(pin)) {

                System.out.println(
                        "\nLogin successful."
                );

                customerMenu(account);

                return;

            } else {

                attempts--;

                System.out.println(
                        "Incorrect PIN. Attempts remaining: "
                                + attempts
                );
            }
        }

        System.out.println(
                "Maximum login attempts reached."
        );
    }

    private static void customerMenu(
            Account account) {

        boolean loggedIn = true;

        while (loggedIn && account.isActive()) {

            System.out.println(
                    "\n========== CUSTOMER MENU =========="
            );

            System.out.println(
                    "1. Deposit"
            );

            System.out.println(
                    "2. Withdraw"
            );

            System.out.println(
                    "3. Transfer"
            );

            System.out.println(
                    "4. Check Balance"
            );

            System.out.println(
                    "5. Transaction History"
            );

            System.out.println(
                    "6. Account Statement"
            );

            System.out.println(
                    "7. Account Details"
            );

            System.out.println(
                    "8. Calculate Interest"
            );

            System.out.println(
                    "9. Close Account"
            );

            System.out.println(
                    "10. Manage Profile"
            );

            System.out.println(
                    "11. Logout"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    deposit(account);
                    break;

                case 2:
                    withdraw(account);
                    break;

                case 3:
                    transfer(account);
                    break;

                case 4:
                    checkBalance(account);
                    break;

                case 5:
                    displayTransactions(account);
                    break;

                case 6:
                    statementService.generateStatement(
                            account
                    );
                    break;

                case 7:
                    account.displayDetails();
                    break;

                case 8:
                    calculateInterest(account);
                    break;

                case 9:
                    closeCustomerAccount(account);
                    break;

                case 10:
                    manageProfile(account);
                    break;

                case 11:
                    loggedIn = false;

                    System.out.println(
                            "Logged out successfully."
                    );

                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void deposit(Account account) {

        double amount =
                readDouble(
                        "Enter deposit amount: "
                );

        if (!InputValidator.isValidAmount(amount)) {

            System.out.println(
                    "Invalid amount."
            );

            return;
        }

        if (account.deposit(amount)) {

            bank.saveAccounts();

            System.out.printf(
                    "Deposit successful. New balance: %.2f%n",
                    account.getBalance()
            );

        } else {

            System.out.println(
                    "Deposit failed."
            );
        }
    }

    private static void withdraw(Account account) {

        double amount =
                readDouble(
                        "Enter withdrawal amount: "
                );

        if (!InputValidator.isValidAmount(amount)) {

            System.out.println(
                    "Invalid amount."
            );

            return;
        }

        if (account instanceof SavingsAccount) {

            if (amount > account.getBalance()) {

                System.out.println(
                        "Insufficient balance."
                );

                return;
            }

        } else if (account instanceof CurrentAccount) {

            CurrentAccount currentAccount =
                    (CurrentAccount) account;

            if (amount >
                    account.getBalance()
                            + currentAccount.getOverdraftLimit()) {

                System.out.println(
                        "Amount exceeds available balance "
                                + "and overdraft limit."
                );

                return;
            }
        }

        if (account.withdraw(amount)) {

            bank.saveAccounts();

            System.out.printf(
                    "Withdrawal successful. "
                            + "New balance: %.2f%n",
                    account.getBalance()
            );

        } else {

            System.out.println(
                    "Withdrawal failed."
            );
        }
    }

    private static void transfer(Account account) {

        System.out.print(
                "Enter receiver account number: "
        );

        String receiverNumber =
                scanner.nextLine().trim();

        double amount =
                readDouble(
                        "Enter transfer amount: "
                );

        if (!InputValidator.isValidAmount(amount)) {

            System.out.println(
                    "Invalid amount."
            );

            return;
        }

        if (bank.transfer(
                account.getAccountNumber(),
                receiverNumber,
                amount)) {

            System.out.println(
                    "Transfer successful."
            );

            System.out.printf(
                    "New balance: %.2f%n",
                    account.getBalance()
            );

        } else {

            System.out.println(
                    "Transfer failed."
            );
        }
    }

    private static void checkBalance(
            Account account) {

        System.out.printf(
                "\nCurrent Balance: %.2f%n",
                account.getBalance()
        );
    }

    private static void displayTransactions(
            Account account) {

        System.out.println(
                "\n========== TRANSACTION HISTORY =========="
        );

        if (account.getTransactions().isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

        } else {

            for (var transaction :
                    account.getTransactions()) {

                System.out.println(transaction);
            }
        }

        System.out.println(
                "========================================="
        );
    }

    private static void calculateInterest(
            Account account) {

        if (account instanceof SavingsAccount) {

            SavingsAccount savings =
                    (SavingsAccount) account;

            double interest =
                    savings.calculateInterest();

            System.out.printf(
                    "\nInterest at %.2f%%: %.2f%n",
                    savings.getInterestRate(),
                    interest
            );

        } else {

            System.out.println(
                    "\nCurrent Account does not provide "
                            + "savings interest."
            );
        }
    }

    private static void closeCustomerAccount(
            Account account) {

        if (account.getBalance() != 0) {

            System.out.println(
                    "\nAccount cannot be closed."
            );

            System.out.println(
                    "Please withdraw or transfer the "
                            + "remaining balance first."
            );

            return;
        }

        System.out.print(
                "Are you sure you want to close "
                        + "your account? (yes/no): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("yes")) {

            System.out.println(
                    "Account closure cancelled."
            );

            return;
        }

        if (account.closeAccount()) {

            bank.saveAccounts();

            System.out.println(
                    "Account closed successfully."
            );

        } else {

            System.out.println(
                    "Account closure failed."
            );
        }
    }

    private static void manageProfile(
            Account account) {

        boolean managing = true;

        while (managing) {

            System.out.println(
                    "\n========== MANAGE PROFILE =========="
            );

            System.out.println(
                    "1. View Profile"
            );

            System.out.println(
                    "2. Update Name"
            );

            System.out.println(
                    "3. Update Phone Number"
            );

            System.out.println(
                    "4. Update Email"
            );

            System.out.println(
                    "5. Update Address"
            );

            System.out.println(
                    "6. Change PIN"
            );

            System.out.println(
                    "7. Back"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    account.getCustomer()
                            .displayProfile();
                    break;

                case 2:
                    updateName(account);
                    break;

                case 3:
                    updatePhone(account);
                    break;

                case 4:
                    updateEmail(account);
                    break;

                case 5:
                    updateAddress(account);
                    break;

                case 6:
                    changePin(account);
                    break;

                case 7:
                    managing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void updateName(Account account) {

        System.out.print(
                "Enter new name: "
        );

        String name =
                scanner.nextLine().trim();

        if (!InputValidator.isValidName(name)) {

            System.out.println(
                    "Invalid name."
            );

            return;
        }

        account.getCustomer().setName(name);

        bank.saveAccounts();

        System.out.println(
                "Name updated successfully."
        );
    }

    private static void updatePhone(Account account) {

        System.out.print(
                "Enter new 10-digit phone number: "
        );

        String phone =
                scanner.nextLine().trim();

        if (!InputValidator.isValidPhone(phone)) {

            System.out.println(
                    "Invalid phone number."
            );

            return;
        }

        account.getCustomer().setPhone(phone);

        bank.saveAccounts();

        System.out.println(
                "Phone number updated successfully."
        );
    }

    private static void updateEmail(Account account) {

        System.out.print(
                "Enter new email: "
        );

        String email =
                scanner.nextLine().trim();

        if (!InputValidator.isValidEmail(email)) {

            System.out.println(
                    "Invalid email."
            );

            return;
        }

        account.getCustomer().setEmail(email);

        bank.saveAccounts();

        System.out.println(
                "Email updated successfully."
        );
    }

    private static void updateAddress(Account account) {

        System.out.print(
                "Enter new address: "
        );

        String address =
                scanner.nextLine().trim();

        if (address.isEmpty()) {

            System.out.println(
                    "Address cannot be empty."
            );

            return;
        }

        account.getCustomer().setAddress(address);

        bank.saveAccounts();

        System.out.println(
                "Address updated successfully."
        );
    }

    private static void changePin(Account account) {

        System.out.print(
                "Enter current PIN: "
        );

        String oldPin =
                scanner.nextLine().trim();

        System.out.print(
                "Enter new 4-digit PIN: "
        );

        String newPin =
                scanner.nextLine().trim();

        if (!InputValidator.isValidPin(newPin)) {

            System.out.println(
                    "New PIN must contain exactly 4 digits."
            );

            return;
        }

        System.out.print(
                "Confirm new PIN: "
        );

        String confirmPin =
                scanner.nextLine().trim();

        if (!newPin.equals(confirmPin)) {

            System.out.println(
                    "PIN confirmation does not match."
            );

            return;
        }

        if (account.changePin(oldPin, newPin)) {

            bank.saveAccounts();

            System.out.println(
                    "PIN changed successfully."
            );

        } else {

            System.out.println(
                    "Incorrect current PIN or invalid new PIN."
            );
        }
    }

    private static void adminLogin() {

        System.out.println(
                "\n========== ADMIN LOGIN =========="
        );

        System.out.print(
                "Username: "
        );

        String username =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        if (bank.verifyAdmin(
                username,
                password)) {

            System.out.println(
                    "\nAdmin login successful."
            );

            adminMenu();

        } else {

            System.out.println(
                    "Invalid admin credentials."
            );
        }
    }

    private static void adminMenu() {

        boolean loggedIn = true;

        while (loggedIn) {

            System.out.println(
                    "\n========== ADMIN MENU =========="
            );

            System.out.println(
                    "1. View All Accounts"
            );

            System.out.println(
                    "2. Search Account"
            );

            System.out.println(
                    "3. View Account Details"
            );

            System.out.println(
                    "4. View Account Transactions"
            );

            System.out.println(
                    "5. Close Account"
            );

            System.out.println(
                    "6. Bank Statistics"
            );

            System.out.println(
                    "7. Savings Accounts Count"
            );

            System.out.println(
                    "8. Current Accounts Count"
            );

            System.out.println(
                    "9. Active Accounts Count"
            );

            System.out.println(
                    "10. Closed Accounts Count"
            );

            System.out.println(
                    "11. Logout"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    bank.displayAllAccounts();
                    break;

                case 2:
                    adminSearchAccount();
                    break;

                case 3:
                    adminViewAccountDetails();
                    break;

                case 4:
                    adminViewTransactions();
                    break;

                case 5:
                    adminCloseAccount();
                    break;

                case 6:
                    displayBankStatistics();
                    break;

                case 7:
                    System.out.println(
                            "Savings Accounts: "
                                    + bank.getSavingsAccounts()
                    );
                    break;

                case 8:
                    System.out.println(
                            "Current Accounts: "
                                    + bank.getCurrentAccounts()
                    );
                    break;

                case 9:
                    System.out.println(
                            "Active Accounts: "
                                    + bank.getActiveAccounts()
                    );
                    break;

                case 10:
                    System.out.println(
                            "Closed Accounts: "
                                    + bank.getClosedAccounts()
                    );
                    break;

                case 11:
                    loggedIn = false;

                    System.out.println(
                            "Admin logged out successfully."
                    );

                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void adminSearchAccount() {

        System.out.print(
                "Enter customer name to search: "
        );

        String name =
                scanner.nextLine().trim();

        List<Account> results =
                bank.searchAccountsByName(name);

        if (results.isEmpty()) {

            System.out.println(
                    "No matching accounts found."
            );

            return;
        }

        System.out.println(
                "\n========== SEARCH RESULTS =========="
        );

        for (Account account : results) {

            System.out.println(
                    "Account Number : "
                            + account.getAccountNumber()
            );

            System.out.println(
                    "Customer Name  : "
                            + account.getCustomer().getName()
            );

            System.out.println(
                    "Account Type   : "
                            + account.getAccountType()
            );

            System.out.println(
                    "Status         : "
                            + (account.isActive()
                            ? "ACTIVE"
                            : "CLOSED")
            );

            System.out.println(
                    "------------------------------------"
            );
        }
    }

    private static void adminViewAccountDetails() {

        System.out.print(
                "Enter account number: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        Account account =
                bank.getAccountForAdmin(accountNumber);

        if (account == null) {

            System.out.println(
                    "Account not found."
            );

            return;
        }

        account.displayDetails();
    }

    private static void adminViewTransactions() {

        System.out.print(
                "Enter account number: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        bank.displayAccountTransactions(
                accountNumber
        );
    }

    private static void adminCloseAccount() {

        System.out.print(
                "Enter account number: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        Account account =
                bank.getAccountForAdmin(accountNumber);

        if (account == null) {

            System.out.println(
                    "Account not found."
            );

            return;
        }

        if (account.getBalance() != 0) {

            System.out.println(
                    "Account cannot be closed because "
                            + "the balance is not zero."
            );

            return;
        }

        System.out.print(
                "Confirm account closure (yes/no): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("yes")) {

            System.out.println(
                    "Account closure cancelled."
            );

            return;
        }

        if (bank.closeAccountByAdmin(
                accountNumber)) {

            System.out.println(
                    "Account closed successfully."
            );

        } else {

            System.out.println(
                    "Account closure failed."
            );
        }
    }

    private static void displayBankStatistics() {

        System.out.println(
                "\n========== BANK STATISTICS =========="
        );

        System.out.println(
                "Total Accounts   : "
                        + bank.getTotalAccounts()
        );

        System.out.println(
                "Active Accounts  : "
                        + bank.getActiveAccounts()
        );

        System.out.println(
                "Closed Accounts  : "
                        + bank.getClosedAccounts()
        );

        System.out.println(
                "Savings Accounts : "
                        + bank.getSavingsAccounts()
        );

        System.out.println(
                "Current Accounts : "
                        + bank.getCurrentAccounts()
        );

        System.out.printf(
                "Total Bank Balance: %.2f%n",
                bank.getTotalBankBalance()
        );

        System.out.println(
                "====================================="
        );
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid integer."
                );
            }
        }
    }

    private static double readDouble(String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {

                return Double.parseDouble(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }
}