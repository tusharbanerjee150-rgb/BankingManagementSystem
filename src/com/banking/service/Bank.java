package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Admin;
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

    private static final String DATA_FILE = "data/accounts.dat";

    private Admin admin;

    public Bank() {

        accounts = new ArrayList<>();

        admin = new Admin(
                "admin",
                "admin123"
        );

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

        if (!InputValidator.isValidName(name)
                || !InputValidator.isValidPhone(phone)
                || !InputValidator.isValidEmail(email)
                || !InputValidator.isValidPin(pin)
                || !InputValidator.isValidAmount(initialDeposit)) {

            return null;
        }

        if (!accountType.equalsIgnoreCase("savings")
                && !accountType.equalsIgnoreCase("current")) {

            return null;
        }

        String customerId = generateCustomerId();
        String accountNumber = generateAccountNumber();

        Customer customer = new Customer(
                customerId,
                name,
                phone,
                email,
                address
        );

        Account account;

        if (accountType.equalsIgnoreCase("savings")) {

            account = new SavingsAccount(
                    accountNumber,
                    customer,
                    pin,
                    initialDeposit
            );

        } else {

            account = new CurrentAccount(
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

        return account;
    }

    private String generateAccountNumber() {

        long highestNumber = 99999;

        for (Account account : accounts) {

            try {

                long number = Long.parseLong(
                        account.getAccountNumber()
                );

                if (number > highestNumber) {
                    highestNumber = number;
                }

            } catch (NumberFormatException ignored) {
            }
        }

        return String.valueOf(highestNumber + 1);
    }

    private String generateCustomerId() {

        int highestNumber = 0;

        for (Account account : accounts) {

            String customerId =
                    account.getCustomer().getCustomerId();

            if (customerId != null
                    && customerId.startsWith("C")) {

                try {

                    int number = Integer.parseInt(
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

    public Account findAccount(String accountNumber) {

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

        if (!InputValidator.isValidAmount(amount)) {
            return false;
        }

        Account sender = findAccount(senderNumber);
        Account receiver = findAccount(receiverNumber);

        if (sender == null || receiver == null) {
            return false;
        }

        if (!sender.isActive()
                || !receiver.isActive()) {

            return false;
        }

        if (senderNumber.equals(receiverNumber)) {
            return false;
        }

        /*
         * Savings accounts cannot transfer more than
         * their available balance.
         */
        if (sender instanceof SavingsAccount) {

            if (amount > sender.getBalance()) {
                return false;
            }

        /*
         * Current accounts can use their overdraft limit.
         */
        } else if (sender instanceof CurrentAccount) {

            CurrentAccount currentAccount =
                    (CurrentAccount) sender;

            if (amount >
                    sender.getBalance()
                            + currentAccount.getOverdraftLimit()) {

                return false;
            }
        }

        /*
         * Remove the amount from the sender.
         */
        if (!sender.transferOut(amount)) {
            return false;
        }

        /*
         * Add the amount to the receiver.
         * If this fails, restore the sender's balance.
         */
        if (!receiver.transferIn(amount)) {

            sender.transferIn(amount);

            return false;
        }

        /*
         * Record a separate transaction for the sender.
         */
        sender.addTransaction(
                "TRANSFER SENT",
                amount,
                "Transfer sent to account "
                        + receiverNumber
        );

        /*
         * Record a separate transaction for the receiver.
         */
        receiver.addTransaction(
                "TRANSFER RECEIVED",
                amount,
                "Transfer received from account "
                        + senderNumber
        );

        saveAccounts();

        return true;
    }

    public boolean verifyAdmin(
            String username,
            String password) {

        return admin.getUsername().equals(username)
                && admin.verifyPassword(password);
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
                            + account.getCustomer().getName()
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
                    .contains(name.toLowerCase())) {

                results.add(account);
            }
        }

        return results;
    }

    public boolean closeAccountByAdmin(
            String accountNumber) {

        Account account =
                findAccount(accountNumber);

        if (account == null) {
            return false;
        }

        if (!account.isActive()) {
            return false;
        }

        if (account.getBalance() != 0) {
            return false;
        }

        boolean closed =
                account.closeAccount();

        if (closed) {
            saveAccounts();
        }

        return closed;
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
                    (List<Account>) input.readObject();

        } catch (IOException
                 | ClassNotFoundException e) {

            System.out.println(
                    "Error loading account data: "
                            + e.getMessage()
            );

            accounts = new ArrayList<>();
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}