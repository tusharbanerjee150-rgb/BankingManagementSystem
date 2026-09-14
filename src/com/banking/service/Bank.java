package com.banking.service;

import com.banking.admin.Admin;
import com.banking.exception.BankingException;
import com.banking.model.Account;
import com.banking.model.Beneficiary;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bank {

    private static final double MAX_TRANSFER_AMOUNT = 50000.00;

    private static final double DAILY_TRANSFER_LIMIT = 100000.00;

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
                    address,
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
            String address,
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

        if (address == null || address.trim().isEmpty()) {

            throw new BankingException(
                    "Address cannot be empty."
            );
        }

        if (isPhoneAlreadyRegistered(phone)) {

            throw new BankingException(
                    "Phone number is already registered."
            );
        }

        if (isEmailAlreadyRegistered(email)) {

            throw new BankingException(
                    "Email address is already registered."
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

    private boolean isPhoneAlreadyRegistered(String phone) {

        for (Account account : accounts) {

            if (account.getCustomer().getPhone()
                    .equalsIgnoreCase(phone.trim())) {
                return true;
            }
        }

        return false;
    }

    private boolean isEmailAlreadyRegistered(String email) {

        for (Account account : accounts) {

            if (account.getCustomer().getEmail()
                    .equalsIgnoreCase(email.trim())) {
                return true;
            }
        }

        return false;
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

        if (amount > MAX_TRANSFER_AMOUNT) {

            throw new BankingException(
                    "Transfer amount cannot exceed 50000.00 per transaction."
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

        double todayTransferTotal =
                getTodayTransferTotal(sender);

        if (todayTransferTotal + amount > DAILY_TRANSFER_LIMIT) {

            throw new BankingException(
                    "Daily transfer limit of 100000.00 exceeded. "
                            + "Today's outgoing transfers: "
                            + String.format("%.2f", todayTransferTotal)
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

        if (receiver.isFrozen()) {

            throw new BankingException(
                    "Receiver account is frozen."
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

    private double getTodayTransferTotal(
            Account account) {

        double total = 0;
        LocalDate today = LocalDate.now();

        for (Transaction transaction :
                account.getTransactions()) {

            if ("TRANSFER SENT".equalsIgnoreCase(
                    transaction.getType())
                    && transaction.getTimestamp() != null
                    && transaction.getTimestamp()
                    .toLocalDate()
                    .equals(today)) {

                total += transaction.getAmount();
            }
        }

        return total;
    }

    public boolean transferToBeneficiary(
            String senderNumber,
            String beneficiaryAccountNumber,
            double amount) {

        Account sender =
                findAccount(senderNumber);

        if (sender == null) {
            System.out.println(
                    "Transfer error: Sender account not found."
            );
            return false;
        }

        Beneficiary beneficiary =
                sender.getCustomer()
                        .findBeneficiary(
                                beneficiaryAccountNumber
                        );

        if (beneficiary == null) {
            System.out.println(
                    "Transfer error: Beneficiary is not saved."
            );
            return false;
        }

        return transfer(
                senderNumber,
                beneficiary.getAccountNumber(),
                amount
        );
    }

    public boolean addBeneficiary(
            String ownerAccountNumber,
            String beneficiaryAccountNumber) {

        Account owner =
                findAccount(ownerAccountNumber);

        if (owner == null) {
            System.out.println(
                    "Beneficiary error: Owner account not found."
            );
            return false;
        }

        if (!owner.isActive()) {
            System.out.println(
                    "Beneficiary error: Owner account is closed."
            );
            return false;
        }

        Account beneficiaryAccount =
                findAccount(beneficiaryAccountNumber);

        if (beneficiaryAccount == null) {
            System.out.println(
                    "Beneficiary error: Account not found."
            );
            return false;
        }

        if (!beneficiaryAccount.isActive()) {
            System.out.println(
                    "Beneficiary error: Beneficiary account is closed."
            );
            return false;
        }

        if (ownerAccountNumber.equals(
                beneficiaryAccountNumber)) {

            System.out.println(
                    "Beneficiary error: You cannot add your own account."
            );
            return false;
        }

        Beneficiary beneficiary =
                new Beneficiary(
                        beneficiaryAccount.getAccountNumber(),
                        beneficiaryAccount.getCustomer().getName(),
                        beneficiaryAccount.getAccountType()
                );

        if (!owner.getCustomer()
                .addBeneficiary(beneficiary)) {

            System.out.println(
                    "Beneficiary error: Beneficiary already exists."
            );
            return false;
        }

        saveAccounts();

        return true;
    }

    public boolean removeBeneficiary(
            String ownerAccountNumber,
            String beneficiaryAccountNumber) {

        Account owner =
                findAccount(ownerAccountNumber);

        if (owner == null) {
            System.out.println(
                    "Beneficiary error: Owner account not found."
            );
            return false;
        }

        if (!owner.isActive()) {
            System.out.println(
                    "Beneficiary error: Owner account is closed."
            );
            return false;
        }

        if (!owner.getCustomer()
                .removeBeneficiary(
                        beneficiaryAccountNumber
                )) {

            System.out.println(
                    "Beneficiary error: Beneficiary not found."
            );
            return false;
        }

        saveAccounts();

        return true;
    }

    public List<Beneficiary> getBeneficiaries(
            String ownerAccountNumber) {

        Account owner =
                findAccount(ownerAccountNumber);

        if (owner == null) {
            return new ArrayList<>();
        }

        return owner.getCustomer()
                .getBeneficiaries();
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

    public Admin getAdmin() {
        return admin;
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    public int getFrozenAccounts() {

        int count = 0;

        for (Account account : accounts) {
            if (account.isFrozen()) {
                count++;
            }
        }

        return count;
    }

    public boolean freezeAccountByAdmin(
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
                        "Closed account cannot be frozen."
                );
            }

            if (account.isFrozen()) {
                throw new BankingException(
                        "Account is already frozen."
                );
            }

            if (!account.freezeAccount()) {
                throw new BankingException(
                        "Unable to freeze account."
                );
            }

            saveAccounts();

            auditLogger.log(
                    "ADMIN ACCOUNT FREEZE | Account: "
                            + accountNumber
            );

            return true;

        } catch (BankingException e) {

            System.out.println(
                    "Account freeze error: "
                            + e.getMessage()
            );

            return false;
        }
    }

    public boolean unfreezeAccountByAdmin(
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
                        "Closed account cannot be unfrozen."
                );
            }

            if (!account.isFrozen()) {
                throw new BankingException(
                        "Account is not frozen."
                );
            }

            if (!account.unfreezeAccount()) {
                throw new BankingException(
                        "Unable to unfreeze account."
                );
            }

            saveAccounts();

            auditLogger.log(
                    "ADMIN ACCOUNT UNFREEZE | Account: "
                            + accountNumber
            );

            return true;

        } catch (BankingException e) {

            System.out.println(
                    "Account unfreeze error: "
                            + e.getMessage()
            );

            return false;
        }
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

    public int getTotalTransactions() {

        int total = 0;

        for (Account account : accounts) {
            total += account.getTransactions().size();
        }

        return total;
    }

    public double getTotalDeposits() {

        double total = 0;

        for (Account account : accounts) {
            for (Transaction transaction :
                    account.getTransactions()) {

                if ("DEPOSIT".equalsIgnoreCase(
                        transaction.getType())) {
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }

    public double getTotalWithdrawals() {

        double total = 0;

        for (Account account : accounts) {
            for (Transaction transaction :
                    account.getTransactions()) {

                if ("WITHDRAW".equalsIgnoreCase(
                        transaction.getType())) {
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }

    public double getTotalTransfersSent() {

        double total = 0;

        for (Account account : accounts) {
            for (Transaction transaction :
                    account.getTransactions()) {

                if ("TRANSFER SENT".equalsIgnoreCase(
                        transaction.getType())) {
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }

    public double getTotalTransfersReceived() {

        double total = 0;

        for (Account account : accounts) {
            for (Transaction transaction :
                    account.getTransactions()) {

                if ("TRANSFER RECEIVED".equalsIgnoreCase(
                        transaction.getType())) {
                    total += transaction.getAmount();
                }
            }
        }

        return total;
    }

    public int getTotalBeneficiaries() {

        int total = 0;

        for (Account account : accounts) {
            if (account.getCustomer() != null
                    && account.getCustomer()
                    .getBeneficiaries() != null) {

                total += account.getCustomer()
                        .getBeneficiaries()
                        .size();
            }
        }

        return total;
    }

    public void displayAdminDashboard() {

        System.out.println(
                "\n=============================================="
        );
        System.out.println(
                "              ADMIN DASHBOARD"
        );
        System.out.println(
                "=============================================="
        );

        System.out.println(
                "ACCOUNT OVERVIEW"
        );
        System.out.println(
                "----------------------------------------------"
        );
        System.out.println(
                "Total Accounts       : " + getTotalAccounts()
        );
        System.out.println(
                "Active Accounts      : " + getActiveAccounts()
        );
        System.out.println(
                "Frozen Accounts      : " + getFrozenAccounts()
        );
        System.out.println(
                "Closed Accounts      : " + getClosedAccounts()
        );
        System.out.println(
                "Savings Accounts     : " + getSavingsAccounts()
        );
        System.out.println(
                "Current Accounts     : " + getCurrentAccounts()
        );

        System.out.printf(
                "Total Bank Balance   : %.2f%n",
                getTotalBankBalance()
        );

        System.out.println(
                "\nTRANSACTION OVERVIEW"
        );
        System.out.println(
                "----------------------------------------------"
        );
        System.out.println(
                "Total Transactions    : " + getTotalTransactions()
        );

        System.out.printf(
                "Total Deposits       : %.2f%n",
                getTotalDeposits()
        );

        System.out.printf(
                "Total Withdrawals    : %.2f%n",
                getTotalWithdrawals()
        );

        System.out.printf(
                "Transfers Sent       : %.2f%n",
                getTotalTransfersSent()
        );

        System.out.printf(
                "Transfers Received   : %.2f%n",
                getTotalTransfersReceived()
        );

        System.out.println(
                "\nOTHER INFORMATION"
        );
        System.out.println(
                "----------------------------------------------"
        );
        System.out.println(
                "Saved Beneficiaries  : " + getTotalBeneficiaries()
        );

        System.out.println(
                "=============================================="
        );
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
                            + account.getStatus()
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