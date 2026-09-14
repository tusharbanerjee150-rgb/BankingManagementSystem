package com.banking.service;

import com.banking.admin.Admin;
import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.CurrentAccount;
import com.banking.model.Customer;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;
import com.banking.validation.InputValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private List<Account> accounts;

    private static final String DATA_FILE =
            "data/accounts.dat";

    private Admin admin;

    private AuditLogger auditLogger;

    public Bank() {

        accounts = new ArrayList<>();

        admin = new Admin(
                "admin",
                "admin123"
        );

        auditLogger = new AuditLogger();

        loadAccounts();
        initializeTransactionCounter();
    }

    public Account createAccount(
            String name,
            String phone,
            String email,
            String address,
            String pin,
            String accountType,
            double initialDeposit) {

        try {

            validateAccountCreation(
                    name,
                    phone,
                    email,
                    pin,
                    accountType,
                    initialDeposit
            );

            String customerId =
                    generateCustomerId();

            String accountNumber =
                    generateAccountNumber();

            Customer customer =
                    new Customer(
                            customerId,
                            name,
                            phone,
                            email,
                            address
                    );

            Account account;

            if (accountType.equalsIgnoreCase("savings")) {

                account =
                        new SavingsAccount(
                                accountNumber,
                                customer,
                                pin,
                                initialDeposit
                        );

            } else {

                account =
                        new CurrentAccount(
                                accountNumber,
                                customer,
                                pin,
                                initialDeposit
                        );
            }

            account.addTransaction(
                    "OPENING",
                    initialDeposit,
                    "Account opened with initial deposit"
            );

            accounts.add(account);

            saveAccounts();

            auditLogger.log(
                    "ACCOUNT CREATED | Account: "
                            + accountNumber
                            + " | Customer: "
                            + customerId
                            + " | Type: "
                            + account.getAccountType()
            );

            return account;

        } catch (BankingException e) {

            System.out.println(
                    "Account creation error: "
                            + e.getMessage()
            );

            return null;
        }
    }

    private void validateAccountCreation(
            String name,
            String phone,
            String email,
            String pin,
            String accountType,
            double initialDeposit)
            throws BankingException {

        if (!InputValidator.isValidName(name)) {

            throw new BankingException(
                    "Invalid customer name."
            );
        }

        if (!InputValidator.isValidPhone(phone)) {

            throw new BankingException(
                    "Invalid phone number."
            );
        }

        if (!InputValidator.isValidEmail(email)) {

            throw new BankingException(
                    "Invalid email address."
            );
        }

        if (!InputValidator.isValidPin(pin)) {

            throw new BankingException(
                    "PIN must contain exactly 4 digits."
            );
        }

        if (!InputValidator.isValidAmount(
                initialDeposit)) {

            throw new BankingException(
                    "Initial deposit must be greater than 0."
            );
        }

        if (accountType == null
                || (!accountType.equalsIgnoreCase("savings")
                && !accountType.equalsIgnoreCase("current"))) {

            throw new BankingException(
                    "Invalid account type."
            );
        }
    }

    private String generateAccountNumber() {

        long highestNumber = 99999;

        for (Account account : accounts) {

            try {

                long number =
                        Long.parseLong(
                                account.getAccountNumber()
                        );

                if (number > highestNumber) {
                    highestNumber = number;
                }

            } catch (NumberFormatException ignored) {
            }
        }

        return String.valueOf(
                highestNumber + 1
        );
    }

    private String generateCustomerId() {

        int highestNumber = 0;

        for (Account account : accounts) {

            String customerId =
                    account.getCustomer()
                            .getCustomerId();

            if (customerId != null
                    && customerId.startsWith("C")) {

                try {

                    int number =
                            Integer.parseInt(
                                    customerId.substring(1)
                            );

                    if (number > highestNumber) {
                        highestNumber = number;
                    }

                } catch (NumberFormatException ignored) {
                }
            }
        }

        return String.format(
                "C%04d",
                highestNumber + 1
        );
    }

    public Account findAccount(
            String accountNumber) {

        for (Account account : accounts) {

            if (account.getAccountNumber()
                    .equals(accountNumber)) {

                return account;
            }
        }

        return null;
    }

    public boolean transfer(
            String senderNumber,
            String receiverNumber,
            double amount) {

        try {

            validateTransfer(
                    senderNumber,
                    receiverNumber,
                    amount
            );

            Account sender =
                    findAccount(senderNumber);

            Account receiver =
                    findAccount(receiverNumber);

            if (!sender.transferOut(amount)) {

                throw new BankingException(
                        "Unable to withdraw transfer amount."
                );
            }

            if (!receiver.transferIn(amount)) {

                sender.transferIn(amount);

                throw new BankingException(
                        "Unable to credit receiver account."
                );
            }

            sender.addTransaction(
                    "TRANSFER SENT",
                    amount,
                    "Transfer sent to account "
                            + receiverNumber
            );

            receiver.addTransaction(
                    "TRANSFER RECEIVED",
                    amount,
                    "Transfer received from account "
                            + senderNumber
            );

            saveAccounts();

            auditLogger.log(
                    "TRANSFER | From: "
                            + senderNumber
                            + " | To: "
                            + receiverNumber
                            + " | Amount: "
                            + String.format(
                                    "%.2f",
                                    amount
                            )
            );

            return true;

        } catch (BankingException e) {

            System.out.println(
                    "Transfer error: "
                            + e.getMessage()
            );

            return false;
        }
    }

    private void validateTransfer(
            String senderNumber,
            String receiverNumber,
            double amount)
            throws BankingException {

        if (!InputValidator.isValidAmount(amount)) {

            throw new BankingException(
                    "Transfer amount must be greater than 0."
            );
        }

        Account sender =
                findAccount(senderNumber);

        Account receiver =
                findAccount(receiverNumber);

        if (sender == null) {

            throw new BankingException(
                    "Sender account not found."
            );
        }

        if (receiver == null) {

            throw new BankingException(
                    "Receiver account not found."
            );
        }

        if (!sender.isActive()) {

            throw new BankingException(
                    "Sender account is closed."
            );
        }

        if (!receiver.isActive()) {

            throw new BankingException(
                    "Receiver account is closed."
            );
        }

        if (senderNumber.equals(receiverNumber)) {

            throw new BankingException(
                    "Sender and receiver accounts "
                            + "cannot be the same."
            );
        }

        if (sender instanceof SavingsAccount) {

            if (amount > sender.getBalance()) {

                throw new BankingException(
                        "Insufficient balance in "
                                + "savings account."
                );
            }

        } else if (sender instanceof CurrentAccount) {

            CurrentAccount currentAccount =
                    (CurrentAccount) sender;

            if (amount >
                    sender.getBalance()
                            + currentAccount
                            .getOverdraftLimit()) {

                throw new BankingException(
                        "Amount exceeds available balance "
                                + "and overdraft limit."
                );
            }
        }
    }

    public boolean verifyAdmin(
            String username,
            String password) {

        boolean verified =
                admin.getUsername()
                        .equals(username)
                        && admin.verifyPassword(password);

        if (verified) {

            auditLogger.log(
                    "ADMIN LOGIN SUCCESS | Username: "
                            + username
            );

        } else {

            auditLogger.log(
                    "ADMIN LOGIN FAILED | Username: "
                            + username
            );
        }

        return verified;
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    public int getTotalAccounts() {
        return accounts.size();
    }

    public int getActiveAccounts() {

        int count = 0;

        for (Account account : accounts) {

            if (account.isActive()) {
                count++;
            }
        }

        return count;
    }

    public int getClosedAccounts() {

        int count = 0;

        for (Account account : accounts) {

            if (!account.isActive()) {
                count++;
            }
        }

        return count;
    }

    public int getSavingsAccounts() {

        int count = 0;

        for (Account account : accounts) {

            if (account instanceof SavingsAccount) {
                count++;
            }
        }

        return count;
    }

    public int getCurrentAccounts() {

        int count = 0;

        for (Account account : accounts) {

            if (account instanceof CurrentAccount) {
                count++;
            }
        }

        return count;
    }

    public double getTotalBankBalance() {

        double total = 0;

        for (Account account : accounts) {

            if (account.isActive()) {
                total += account.getBalance();
            }
        }

        return total;
    }

    public void displayAllAccounts() {

        if (accounts.isEmpty()) {

            System.out.println(
                    "\nNo accounts found."
            );

            return;
        }

        System.out.println(
                "\n========== ALL ACCOUNTS =========="
        );

        for (Account account : accounts) {

            System.out.println(
                    "Account Number : "
                            + account.getAccountNumber()
            );

            System.out.println(
                    "Customer Name  : "
                            + account.getCustomer()
                            .getName()
            );

            System.out.println(
                    "Account Type   : "
                            + account.getAccountType()
            );

            System.out.printf(
                    "Balance        : %.2f%n",
                    account.getBalance()
            );

            System.out.println(
                    "Status         : "
                            + (account.isActive()
                            ? "ACTIVE"
                            : "CLOSED")
            );

            System.out.println(
                    "----------------------------------"
            );
        }
    }

    public List<Account> searchAccountsByName(
            String name) {

        List<Account> results =
                new ArrayList<>();

        if (name == null
                || name.trim().isEmpty()) {

            return results;
        }

        for (Account account : accounts) {

            if (account.getCustomer()
                    .getName()
                    .toLowerCase()
                    .contains(
                            name.toLowerCase()
                    )) {

                results.add(account);
            }
        }

        return results;
    }

    public boolean closeAccountByAdmin(
            String accountNumber) {

        try {

            Account account =
                    findAccount(accountNumber);

            if (account == null) {

                throw new BankingException(
                        "Account not found."
                );
            }

            if (!account.isActive()) {

                throw new BankingException(
                        "Account is already closed."
                );
            }

            if (account.getBalance() != 0) {

                throw new BankingException(
                        "Account balance must be zero "
                                + "before closure."
                );
            }

            if (!account.closeAccount()) {

                throw new BankingException(
                        "Unable to close account."
                );
            }

            saveAccounts();

            auditLogger.log(
                    "ADMIN ACCOUNT CLOSURE | Account: "
                            + accountNumber
            );

            return true;

        } catch (BankingException e) {

            System.out.println(
                    "Account closure error: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public Account getAccountForAdmin(
            String accountNumber) {

        return findAccount(accountNumber);
    }

    public void displayAccountTransactions(
            String accountNumber) {

        Account account =
                findAccount(accountNumber);

        if (account == null) {

            System.out.println(
                    "\nAccount not found."
            );

            return;
        }

        System.out.println(
                "\n========== TRANSACTION HISTORY =========="
        );

        System.out.println(
                "Account Number : "
                        + account.getAccountNumber()
        );

        if (account.getTransactions().isEmpty()) {

            System.out.println(
                    "No transactions found."
            );

        } else {

            for (Transaction transaction :
                    account.getTransactions()) {

                System.out.println(transaction);
            }
        }

        System.out.println(
                "========================================="
        );
    }

    private void initializeTransactionCounter() {

        int highestTransactionId = 1000;

        for (Account account : accounts) {

            for (Transaction transaction :
                    account.getTransactions()) {

                String id =
                        transaction.getTransactionId();

                if (id != null
                        && id.startsWith("TXN")) {

                    try {

                        int number =
                                Integer.parseInt(
                                        id.substring(3)
                                );

                        if (number >
                                highestTransactionId) {

                            highestTransactionId = number;
                        }

                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        Transaction.initializeCounter(
                highestTransactionId
        );
    }

    public void saveAccounts() {

        try {

            File file =
                    new File(DATA_FILE);

            File parent =
                    file.getParentFile();

            if (parent != null
                    && !parent.exists()) {

                parent.mkdirs();
            }

            try (ObjectOutputStream output =
                         new ObjectOutputStream(
                                 new FileOutputStream(file))) {

                output.writeObject(accounts);
            }

        } catch (IOException e) {

            System.out.println(
                    "Error saving account data: "
                            + e.getMessage()
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAccounts() {

        File file =
                new File(DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(file))) {

            accounts =
                    (List<Account>)
                            input.readObject();

        } catch (IOException
                 | ClassNotFoundException e) {

            System.out.println(
                    "Error loading account data: "
                            + e.getMessage()
            );

            accounts =
                    new ArrayList<>();
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}