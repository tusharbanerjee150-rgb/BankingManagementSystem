package com.banking.cli;

import com.banking.admin.Admin;
import com.banking.model.Account;
import com.banking.model.Beneficiary;
import com.banking.model.CurrentAccount;
import com.banking.model.SavingsAccount;
import com.banking.service.AuditLogger;
import com.banking.service.AuthenticationService;
import com.banking.service.Bank;
import com.banking.service.ReportService;
import com.banking.service.StatementService;
import com.banking.validation.InputValidator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Main {

    private static final java.util.Scanner scanner =
            new java.util.Scanner(System.in);

    private static final Bank bank = new Bank();

    private static final StatementService statementService =
            new StatementService();

    private static final AuthenticationService authenticationService =
            new AuthenticationService();

    private static final ReportService reportService =
            new ReportService();

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            displayMainMenu();

            int choice =
                    readInt("Enter your choice: ");

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

            System.out.print("Enter name: ");

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

            System.out.print("Enter email: ");

            email =
                    scanner.nextLine().trim();

            if (InputValidator.isValidEmail(email)) {
                break;
            }

            System.out.println(
                    "Invalid email address."
            );
        }

        String address;

        while (true) {

            System.out.print("Enter address: ");

            address =
                    scanner.nextLine().trim();

            if (InputValidator.isValidAddress(address)) {
                break;
            }

            System.out.println(
                    "Address cannot be empty."
            );
        }

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

        if (account.isFrozen()) {

            System.out.println(
                    "This account is frozen. "
                            + "Please contact the administrator."
            );

            return;
        }

        int attemptsUsed = 0;
        int maxAttempts =
                authenticationService.getMaxLoginAttempts();

        while (authenticationService.hasAttemptsRemaining(
                attemptsUsed)) {

            System.out.print("Enter PIN: ");

            String pin =
                    scanner.nextLine().trim();

            Account authenticatedAccount =
                    authenticationService.authenticateCustomer(
                            bank,
                            accountNumber,
                            pin
                    );

            if (authenticatedAccount != null) {

                System.out.println(
                        "\nLogin successful."
                );

                customerMenu(authenticatedAccount);

                return;
            }

            attemptsUsed++;

            int remaining =
                    authenticationService.getRemainingAttempts(
                            attemptsUsed
                    );

            if (remaining > 0) {

                System.out.println(
                        "Incorrect PIN. Attempts remaining: "
                                + remaining
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

        while (loggedIn
                && account.isActive()
                && !account.isFrozen()) {

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
                    "11. Manage Beneficiaries"
            );

            System.out.println(
                    "12. Reports"
            );

            System.out.println(
                    "13. Logout"
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
                    transferMenu(account);
                    break;

                case 4:
                    checkBalance(account);
                    break;

                case 5:
                    transactionHistoryMenu(account);
                    break;

                case 6:
                    statementService.generateStatement(account);
                    break;

                case 7:
                    account.displayDetails();
                    break;

                case 8:
                    calculateInterest(account);
                    break;

                case 9:
                    if (closeCustomerAccount(account)) {
                        loggedIn = false;
                    }
                    break;

                case 10:
                    manageProfile(account);
                    break;

                case 11:
                    manageBeneficiaries(account);
                    break;

                case 12:
                    customerReports(account);
                    break;

                case 13:
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

        if (account.isFrozen()) {

            System.out.println(
                    "\nYour session has ended because "
                            + "the account is frozen."
            );
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

    private static void transferMenu(
            Account account) {

        boolean running = true;

        while (running) {

            System.out.println(
                    "\n========== TRANSFER MENU =========="
            );

            System.out.println(
                    "1. Transfer to Account"
            );

            System.out.println(
                    "2. Transfer to Saved Beneficiary"
            );

            System.out.println(
                    "3. Back"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    transfer(account);
                    break;

                case 2:
                    transferToBeneficiary(account);
                    break;

                case 3:
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
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

        if (!InputValidator.isValidAccountNumber(
                receiverNumber)) {

            System.out.println(
                    "Invalid receiver account number."
            );

            return;
        }

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

    private static void transferToBeneficiary(
            Account account) {

        List<Beneficiary> beneficiaries =
                bank.getBeneficiaries(
                        account.getAccountNumber()
                );

        if (beneficiaries.isEmpty()) {

            System.out.println(
                    "\nNo saved beneficiaries."
            );

            return;
        }

        account.getCustomer()
                .displayBeneficiaries();

        System.out.print(
                "Enter beneficiary account number: "
        );

        String beneficiaryAccountNumber =
                scanner.nextLine().trim();

        Beneficiary beneficiary =
                account.getCustomer()
                        .findBeneficiary(
                                beneficiaryAccountNumber
                        );

        if (beneficiary == null) {

            System.out.println(
                    "Beneficiary not found."
            );

            return;
        }

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

        if (bank.transferToBeneficiary(
                account.getAccountNumber(),
                beneficiaryAccountNumber,
                amount)) {

            System.out.println(
                    "Beneficiary transfer successful."
            );

            System.out.printf(
                    "New balance: %.2f%n",
                    account.getBalance()
            );

        } else {

            System.out.println(
                    "Beneficiary transfer failed."
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

    private static void transactionHistoryMenu(
            Account account) {

        boolean viewing = true;

        while (viewing) {

            System.out.println(
                    "\n========== TRANSACTION HISTORY =========="
            );

            System.out.println(
                    "1. All Transactions"
            );

            System.out.println(
                    "2. Deposits"
            );

            System.out.println(
                    "3. Withdrawals"
            );

            System.out.println(
                    "4. Transfers Sent"
            );

            System.out.println(
                    "5. Transfers Received"
            );

            System.out.println(
                    "6. Account Opening"
            );

            System.out.println(
                    "7. PIN Changes"
            );

            System.out.println(
                    "8. Back"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    statementService.filterTransactions(
                            account,
                            "ALL"
                    );
                    break;

                case 2:
                    statementService.filterTransactions(
                            account,
                            "DEPOSIT"
                    );
                    break;

                case 3:
                    statementService.filterTransactions(
                            account,
                            "WITHDRAW"
                    );
                    break;

                case 4:
                    statementService.filterTransactions(
                            account,
                            "TRANSFER SENT"
                    );
                    break;

                case 5:
                    statementService.filterTransactions(
                            account,
                            "TRANSFER RECEIVED"
                    );
                    break;

                case 6:
                    statementService.filterTransactions(
                            account,
                            "OPENING"
                    );
                    break;

                case 7:
                    statementService.filterTransactions(
                            account,
                            "PIN CHANGE"
                    );
                    break;

                case 8:
                    viewing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
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

    private static boolean closeCustomerAccount(
            Account account) {

        if (account.getBalance() != 0) {

            System.out.println(
                    "\nAccount cannot be closed."
            );

            System.out.println(
                    "Please withdraw or transfer the "
                            + "remaining balance first."
            );

            return false;
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

            return false;
        }

        if (account.closeAccount()) {

            bank.saveAccounts();

            System.out.println(
                    "Account closed successfully."
            );

            return true;

        } else {

            System.out.println(
                    "Account closure failed."
            );

            return false;
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

    private static void updateName(
            Account account) {

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

    private static void updatePhone(
            Account account) {

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

        for (Account other :
                bank.getAllAccounts()) {

            if (!other.getAccountNumber()
                    .equals(account.getAccountNumber())
                    && other.getCustomer()
                    .getPhone()
                    .equalsIgnoreCase(phone)) {

                System.out.println(
                        "Phone number is already registered."
                );

                return;
            }
        }

        account.getCustomer().setPhone(phone);

        bank.saveAccounts();

        System.out.println(
                "Phone number updated successfully."
        );
    }

    private static void updateEmail(
            Account account) {

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

        for (Account other :
                bank.getAllAccounts()) {

            if (!other.getAccountNumber()
                    .equals(account.getAccountNumber())
                    && other.getCustomer()
                    .getEmail()
                    .equalsIgnoreCase(email)) {

                System.out.println(
                        "Email address is already registered."
                );

                return;
            }
        }

        account.getCustomer().setEmail(email);

        bank.saveAccounts();

        System.out.println(
                "Email updated successfully."
        );
    }

    private static void updateAddress(
            Account account) {

        System.out.print(
                "Enter new address: "
        );

        String address =
                scanner.nextLine().trim();

        if (!InputValidator.isValidAddress(address)) {

            System.out.println(
                    "Invalid address."
            );

            return;
        }

        account.getCustomer().setAddress(address);

        bank.saveAccounts();

        System.out.println(
                "Address updated successfully."
        );
    }

    private static void changePin(
            Account account) {

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

    private static void manageBeneficiaries(
            Account account) {

        boolean managing = true;

        while (managing) {

            System.out.println(
                    "\n========== BENEFICIARY MANAGEMENT =========="
            );

            System.out.println(
                    "1. View Beneficiaries"
            );

            System.out.println(
                    "2. Add Beneficiary"
            );

            System.out.println(
                    "3. Remove Beneficiary"
            );

            System.out.println(
                    "4. Back"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    account.getCustomer()
                            .displayBeneficiaries();
                    break;

                case 2:
                    addBeneficiary(account);
                    break;

                case 3:
                    removeBeneficiary(account);
                    break;

                case 4:
                    managing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void addBeneficiary(
            Account account) {

        System.out.print(
                "Enter beneficiary account number: "
        );

        String beneficiaryAccountNumber =
                scanner.nextLine().trim();

        if (!InputValidator.isValidAccountNumber(
                beneficiaryAccountNumber)) {

            System.out.println(
                    "Invalid account number."
            );

            return;
        }

        Account beneficiaryAccount =
                bank.findAccount(
                        beneficiaryAccountNumber
                );

        if (beneficiaryAccount == null) {

            System.out.println(
                    "Beneficiary account not found."
            );

            return;
        }

        if (!beneficiaryAccount.isActive()) {

            System.out.println(
                    "Beneficiary account is closed."
            );

            return;
        }

        if (beneficiaryAccount.isFrozen()) {

            System.out.println(
                    "Beneficiary account is frozen."
            );

            return;
        }

        if (bank.addBeneficiary(
                account.getAccountNumber(),
                beneficiaryAccountNumber)) {

            System.out.println(
                    "Beneficiary added successfully."
            );

        } else {

            System.out.println(
                    "Unable to add beneficiary."
            );
        }
    }

    private static void removeBeneficiary(
            Account account) {

        List<Beneficiary> beneficiaries =
                bank.getBeneficiaries(
                        account.getAccountNumber()
                );

        if (beneficiaries.isEmpty()) {

            System.out.println(
                    "No beneficiaries to remove."
            );

            return;
        }

        account.getCustomer()
                .displayBeneficiaries();

        System.out.print(
                "Enter beneficiary account number: "
        );

        String beneficiaryAccountNumber =
                scanner.nextLine().trim();

        if (bank.removeBeneficiary(
                account.getAccountNumber(),
                beneficiaryAccountNumber)) {

            System.out.println(
                    "Beneficiary removed successfully."
            );

        } else {

            System.out.println(
                    "Unable to remove beneficiary."
            );
        }
    }

    private static void customerReports(
            Account account) {

        boolean viewing = true;

        while (viewing) {

            System.out.println(
                    "\n========== CUSTOMER REPORTS =========="
            );

            System.out.println(
                    "1. Monthly Summary"
            );

            System.out.println(
                    "2. Transactions by Date"
            );

            System.out.println(
                    "3. Transactions by Month"
            );

            System.out.println(
                    "4. Transaction Type Total"
            );

            System.out.println(
                    "5. Back"
            );

            int choice =
                    readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    displayCustomerMonthlySummary(account);
                    break;

                case 2:
                    displayTransactionsByDate(account);
                    break;

                case 3:
                    displayTransactionsByMonth(account);
                    break;

                case 4:
                    displayTransactionTypeTotal(account);
                    break;

                case 5:
                    viewing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void displayCustomerMonthlySummary(
            Account account) {

        YearMonth month =
                readYearMonth();

        if (month != null) {

            reportService.displayMonthlySummary(
                    account,
                    month
            );
        }
    }

    private static void displayTransactionsByDate(
            Account account) {

        LocalDate date =
                readDate();

        if (date == null) {
            return;
        }

        List<com.banking.model.Transaction> transactions =
                reportService.getTransactionsForDate(
                        account,
                        date
                );

        System.out.println(
                "\n========== TRANSACTIONS FOR "
                        + date
                        + " =========="
        );

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

        } else {

            for (com.banking.model.Transaction transaction :
                    transactions) {

                System.out.println(transaction);
            }
        }

        System.out.println(
                "=========================================="
        );
    }

    private static void displayTransactionsByMonth(
            Account account) {

        YearMonth month =
                readYearMonth();

        if (month == null) {
            return;
        }

        List<com.banking.model.Transaction> transactions =
                reportService.getTransactionsForMonth(
                        account,
                        month
                );

        System.out.println(
                "\n========== TRANSACTIONS FOR "
                        + month
                        + " =========="
        );

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

        } else {

            for (com.banking.model.Transaction transaction :
                    transactions) {

                System.out.println(transaction);
            }
        }

        System.out.println(
                "=========================================="
        );
    }

    private static void displayTransactionTypeTotal(
            Account account) {

        System.out.println(
                "\nTransaction Types:"
        );

        System.out.println(
                "1. DEPOSIT"
        );

        System.out.println(
                "2. WITHDRAW"
        );

        System.out.println(
                "3. TRANSFER SENT"
        );

        System.out.println(
                "4. TRANSFER RECEIVED"
        );

        int choice =
                readInt("Enter choice: ");

        String type;

        switch (choice) {

            case 1:
                type = "DEPOSIT";
                break;

            case 2:
                type = "WITHDRAW";
                break;

            case 3:
                type = "TRANSFER SENT";
                break;

            case 4:
                type = "TRANSFER RECEIVED";
                break;

            default:
                System.out.println(
                        "Invalid choice."
                );
                return;
        }

        double total =
                reportService.getTotalByType(
                        account,
                        type
                );

        System.out.printf(
                "Total %s amount: %.2f%n",
                type,
                total
        );
    }

    private static void adminLogin() {

        System.out.println(
                "\n========== ADMIN LOGIN =========="
        );

        int attemptsUsed = 0;

        while (authenticationService.hasAttemptsRemaining(
                attemptsUsed)) {

            System.out.print("Username: ");

            String username =
                    scanner.nextLine().trim();

            System.out.print("Password: ");

            String password =
                    scanner.nextLine();

            Admin admin =
                    authenticationService.authenticateAdmin(
                            bank,
                            username,
                            password
                    );

            if (admin != null) {

                System.out.println(
                        "\nAdmin login successful."
                );

                adminMenu();

                return;
            }

            attemptsUsed++;

            int remaining =
                    authenticationService.getRemainingAttempts(
                            attemptsUsed
                    );

            if (remaining > 0) {

                System.out.println(
                        "Invalid admin credentials. "
                                + "Attempts remaining: "
                                + remaining
                );
            }
        }

        System.out.println(
                "Maximum admin login attempts reached."
        );
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
                    "6. Bank Dashboard"
            );

            System.out.println(
                    "7. Account Statistics"
            );

            System.out.println(
                    "8. Freeze Account"
            );

            System.out.println(
                    "9. Unfreeze Account"
            );

            System.out.println(
                    "10. Reports"
            );

            System.out.println(
                    "11. Audit Log Viewer"
            );

            System.out.println(
                    "12. Logout"
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
                    reportService.displayBankSummary(
                            bank.getAllAccounts()
                    );
                    break;

                case 7:
                    displayAccountStatistics();
                    break;

                case 8:
                    adminFreezeAccount();
                    break;

                case 9:
                    adminUnfreezeAccount();
                    break;

                case 10:
                    adminReports();
                    break;

                case 11:
                    auditLogViewer();
                    break;

                case 12:
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
                            + account.getStatus()
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

        Account account =
                bank.getAccountForAdmin(accountNumber);

        if (account == null) {

            System.out.println(
                    "Account not found."
            );

            return;
        }

        adminTransactionFilter(account);
    }

    private static void adminTransactionFilter(
            Account account) {

        boolean viewing = true;

        while (viewing) {

            System.out.println(
                    "\n====== ADMIN TRANSACTION FILTER ======"
            );

            System.out.println(
                    "1. All Transactions"
            );

            System.out.println(
                    "2. Deposits"
            );

            System.out.println(
                    "3. Withdrawals"
            );

            System.out.println(
                    "4. Transfers Sent"
            );

            System.out.println(
                    "5. Transfers Received"
            );

            System.out.println(
                    "6. Account Opening"
            );

            System.out.println(
                    "7. PIN Changes"
            );

            System.out.println(
                    "8. Back"
            );

            int choice =
                    readInt("Enter choice: ");

            switch (choice) {

                case 1:
                    statementService.filterTransactions(
                            account,
                            "ALL"
                    );
                    break;

                case 2:
                    statementService.filterTransactions(
                            account,
                            "DEPOSIT"
                    );
                    break;

                case 3:
                    statementService.filterTransactions(
                            account,
                            "WITHDRAW"
                    );
                    break;

                case 4:
                    statementService.filterTransactions(
                            account,
                            "TRANSFER SENT"
                    );
                    break;

                case 5:
                    statementService.filterTransactions(
                            account,
                            "TRANSFER RECEIVED"
                    );
                    break;

                case 6:
                    statementService.filterTransactions(
                            account,
                            "OPENING"
                    );
                    break;

                case 7:
                    statementService.filterTransactions(
                            account,
                            "PIN CHANGE"
                    );
                    break;

                case 8:
                    viewing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
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

        if (account.isFrozen()) {

            System.out.println(
                    "Frozen account cannot be closed. "
                            + "Unfreeze it first."
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

    private static void displayAccountStatistics() {

        System.out.println(
                "\n========== ACCOUNT STATISTICS =========="
        );

        System.out.println(
                "Total Accounts    : "
                        + bank.getTotalAccounts()
        );

        System.out.println(
                "Active Accounts   : "
                        + bank.getActiveAccounts()
        );

        System.out.println(
                "Frozen Accounts   : "
                        + bank.getFrozenAccounts()
        );

        System.out.println(
                "Closed Accounts   : "
                        + bank.getClosedAccounts()
        );

        System.out.println(
                "Savings Accounts  : "
                        + bank.getSavingsAccounts()
        );

        System.out.println(
                "Current Accounts  : "
                        + bank.getCurrentAccounts()
        );

        System.out.println(
                "Total Beneficiaries: "
                        + bank.getTotalBeneficiaries()
        );

        System.out.printf(
                "Total Bank Balance: %.2f%n",
                bank.getTotalBankBalance()
        );

        System.out.println(
                "Total Transactions: "
                        + bank.getTotalTransactions()
        );

        System.out.println(
                "========================================="
        );
    }

    private static void adminFreezeAccount() {

        System.out.print(
                "Enter account number to freeze: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        if (bank.freezeAccountByAdmin(
                accountNumber)) {

            System.out.println(
                    "Account frozen successfully."
            );

        } else {

            System.out.println(
                    "Account freeze failed."
            );
        }
    }

    private static void adminUnfreezeAccount() {

        System.out.print(
                "Enter account number to unfreeze: "
        );

        String accountNumber =
                scanner.nextLine().trim();

        if (bank.unfreezeAccountByAdmin(
                accountNumber)) {

            System.out.println(
                    "Account unfrozen successfully."
            );

        } else {

            System.out.println(
                    "Account unfreeze failed."
            );
        }
    }

    private static void adminReports() {

        boolean viewing = true;

        while (viewing) {

            System.out.println(
                    "\n========== ADMIN REPORTS =========="
            );

            System.out.println(
                    "1. Bank Summary Report"
            );

            System.out.println(
                    "2. Transaction Statistics"
            );

            System.out.println(
                    "3. Monthly Account Summary"
            );

            System.out.println(
                    "4. Account Activity by Date"
            );

            System.out.println(
                    "5. Account Activity by Month"
            );

            System.out.println(
                    "6. Back"
            );

            int choice =
                    readInt("Enter choice: ");

            switch (choice) {

                case 1:
                    reportService.displayBankSummary(
                            bank.getAllAccounts()
                    );
                    break;

                case 2:
                    reportService.displayTransactionStatistics(
                            bank.getAllAccounts()
                    );
                    break;

                case 3:
                    adminMonthlySummary();
                    break;

                case 4:
                    adminActivityByDate();
                    break;

                case 5:
                    adminActivityByMonth();
                    break;

                case 6:
                    viewing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void adminMonthlySummary() {

        Account account =
                requestAdminAccount();

        if (account == null) {
            return;
        }

        YearMonth month =
                readYearMonth();

        if (month != null) {

            reportService.displayMonthlySummary(
                    account,
                    month
            );
        }
    }

    private static void adminActivityByDate() {

        Account account =
                requestAdminAccount();

        if (account == null) {
            return;
        }

        LocalDate date =
                readDate();

        if (date == null) {
            return;
        }

        List<com.banking.model.Transaction> transactions =
                reportService.getTransactionsForDate(
                        account,
                        date
                );

        displayReportTransactions(
                account,
                "DATE: " + date,
                transactions
        );
    }

    private static void adminActivityByMonth() {

        Account account =
                requestAdminAccount();

        if (account == null) {
            return;
        }

        YearMonth month =
                readYearMonth();

        if (month == null) {
            return;
        }

        List<com.banking.model.Transaction> transactions =
                reportService.getTransactionsForMonth(
                        account,
                        month
                );

        displayReportTransactions(
                account,
                "MONTH: " + month,
                transactions
        );
    }

    private static Account requestAdminAccount() {

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
        }

        return account;
    }

    private static void displayReportTransactions(
            Account account,
            String filter,
            List<com.banking.model.Transaction> transactions) {

        System.out.println(
                "\n========== ACCOUNT ACTIVITY REPORT =========="
        );

        System.out.println(
                "Account Number : "
                        + account.getAccountNumber()
        );

        System.out.println(
                "Filter         : "
                        + filter
        );

        System.out.println(
                "---------------------------------------------"
        );

        if (transactions == null
                || transactions.isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

        } else {

            for (com.banking.model.Transaction transaction :
                    transactions) {

                System.out.println(transaction);
            }
        }

        System.out.println(
                "============================================="
        );
    }

    private static void auditLogViewer() {

        AuditLogger auditLogger =
                bank.getAuditLogger();

        boolean viewing = true;

        while (viewing) {

            System.out.println(
                    "\n========== AUDIT LOG VIEWER =========="
            );

            System.out.println(
                    "1. View All Logs"
            );

            System.out.println(
                    "2. Search Logs"
            );

            System.out.println(
                    "3. Back"
            );

            int choice =
                    readInt("Enter choice: ");

            switch (choice) {

                case 1:
                    auditLogger.displayLogs();
                    break;

                case 2:
                    System.out.print(
                            "Enter search keyword: "
                    );

                    String keyword =
                            scanner.nextLine().trim();

                    if (keyword.isEmpty()) {

                        System.out.println(
                                "Keyword cannot be empty."
                        );

                        break;
                    }

                    auditLogger.displayLogs(
                            auditLogger.getLogsContaining(
                                    keyword
                            )
                    );

                    break;

                case 3:
                    viewing = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static LocalDate readDate() {

        System.out.print(
                "Enter date (YYYY-MM-DD): "
        );

        String input =
                scanner.nextLine().trim();

        try {

            return LocalDate.parse(input);

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid date format."
            );

            return null;
        }
    }

    private static YearMonth readYearMonth() {

        System.out.print(
                "Enter month (YYYY-MM): "
        );

        String input =
                scanner.nextLine().trim();

        try {

            return YearMonth.parse(input);

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid month format."
            );

            return null;
        }
    }

    private static int readInt(
            String message) {

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

    private static double readDouble(
            String message) {

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
