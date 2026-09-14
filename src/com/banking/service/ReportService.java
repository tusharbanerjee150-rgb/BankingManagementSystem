package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public void displayBankSummary(
            List<Account> accounts) {

        System.out.println(
                "\n========== BANK SUMMARY REPORT =========="
        );

        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts available.");
            System.out.println("=========================================");
            return;
        }

        int totalAccounts = accounts.size();
        int activeAccounts = 0;
        int frozenAccounts = 0;
        int closedAccounts = 0;
        int savingsAccounts = 0;
        int currentAccounts = 0;

        double totalBalance = 0;
        int totalTransactions = 0;

        for (Account account : accounts) {

            if (account.isActive()) {
                activeAccounts++;
            }

            if (account.isFrozen()) {
                frozenAccounts++;
            }

            if (!account.isActive()) {
                closedAccounts++;
            }

            if ("SAVINGS".equalsIgnoreCase(
                    account.getAccountType())) {
                savingsAccounts++;
            } else if ("CURRENT".equalsIgnoreCase(
                    account.getAccountType())) {
                currentAccounts++;
            }

            totalBalance += account.getBalance();
            totalTransactions +=
                    account.getTransactions().size();
        }

        System.out.println(
                "Total Accounts      : " + totalAccounts
        );
        System.out.println(
                "Active Accounts     : " + activeAccounts
        );
        System.out.println(
                "Frozen Accounts     : " + frozenAccounts
        );
        System.out.println(
                "Closed Accounts     : " + closedAccounts
        );
        System.out.println(
                "Savings Accounts    : " + savingsAccounts
        );
        System.out.println(
                "Current Accounts    : " + currentAccounts
        );
        System.out.printf(
                "Total Bank Balance  : %.2f%n",
                totalBalance
        );
        System.out.println(
                "Total Transactions  : " + totalTransactions
        );

        System.out.println(
                "========================================="
        );
    }

    public void displayTransactionStatistics(
            List<Account> accounts) {

        System.out.println(
                "\n======= TRANSACTION STATISTICS ======="
        );

        double deposits = 0;
        double withdrawals = 0;
        double transfersSent = 0;
        double transfersReceived = 0;

        int depositCount = 0;
        int withdrawalCount = 0;
        int transferSentCount = 0;
        int transferReceivedCount = 0;

        if (accounts != null) {

            for (Account account : accounts) {

                for (Transaction transaction :
                        account.getTransactions()) {

                    String type =
                            transaction.getType();

                    double amount =
                            transaction.getAmount();

                    if ("DEPOSIT".equalsIgnoreCase(type)) {
                        deposits += amount;
                        depositCount++;
                    } else if ("WITHDRAW".equalsIgnoreCase(type)) {
                        withdrawals += amount;
                        withdrawalCount++;
                    } else if ("TRANSFER SENT"
                            .equalsIgnoreCase(type)) {
                        transfersSent += amount;
                        transferSentCount++;
                    } else if ("TRANSFER RECEIVED"
                            .equalsIgnoreCase(type)) {
                        transfersReceived += amount;
                        transferReceivedCount++;
                    }
                }
            }
        }

        System.out.println(
                "Deposits            : "
                        + depositCount
                        + " transactions"
        );
        System.out.printf(
                "Deposit Amount      : %.2f%n",
                deposits
        );

        System.out.println(
                "Withdrawals         : "
                        + withdrawalCount
                        + " transactions"
        );
        System.out.printf(
                "Withdrawal Amount   : %.2f%n",
                withdrawals
        );

        System.out.println(
                "Transfers Sent      : "
                        + transferSentCount
                        + " transactions"
        );
        System.out.printf(
                "Transfer Sent Amount: %.2f%n",
                transfersSent
        );

        System.out.println(
                "Transfers Received  : "
                        + transferReceivedCount
                        + " transactions"
        );
        System.out.printf(
                "Transfer Received   : %.2f%n",
                transfersReceived
        );

        System.out.println(
                "======================================="
        );
    }

    public void displayMonthlySummary(
            Account account,
            YearMonth month) {

        System.out.println(
                "\n========= MONTHLY SUMMARY ========="
        );

        if (account == null || month == null) {
            System.out.println(
                    "Unable to generate monthly summary."
            );
            System.out.println(
                    "==================================="
            );
            return;
        }

        double deposits = 0;
        double withdrawals = 0;
        double transfersSent = 0;
        double transfersReceived = 0;

        int transactionCount = 0;

        for (Transaction transaction :
                account.getTransactions()) {

            if (transaction.getTimestamp() == null) {
                continue;
            }

            YearMonth transactionMonth =
                    YearMonth.from(
                            transaction.getTimestamp()
                    );

            if (!month.equals(transactionMonth)) {
                continue;
            }

            transactionCount++;

            String type =
                    transaction.getType();

            double amount =
                    transaction.getAmount();

            if ("DEPOSIT".equalsIgnoreCase(type)) {
                deposits += amount;
            } else if ("WITHDRAW"
                    .equalsIgnoreCase(type)) {
                withdrawals += amount;
            } else if ("TRANSFER SENT"
                    .equalsIgnoreCase(type)) {
                transfersSent += amount;
            } else if ("TRANSFER RECEIVED"
                    .equalsIgnoreCase(type)) {
                transfersReceived += amount;
            }
        }

        System.out.println(
                "Account Number : "
                        + account.getAccountNumber()
        );
        System.out.println(
                "Month          : " + month
        );
        System.out.println(
                "Transactions   : " + transactionCount
        );
        System.out.printf(
                "Deposits       : %.2f%n",
                deposits
        );
        System.out.printf(
                "Withdrawals    : %.2f%n",
                withdrawals
        );
        System.out.printf(
                "Transfers Sent : %.2f%n",
                transfersSent
        );
        System.out.printf(
                "Transfers In   : %.2f%n",
                transfersReceived
        );

        System.out.println(
                "==================================="
        );
    }

    public List<Transaction> getTransactionsForDate(
            Account account,
            LocalDate date) {

        List<Transaction> result =
                new ArrayList<>();

        if (account == null || date == null) {
            return result;
        }

        for (Transaction transaction :
                account.getTransactions()) {

            if (transaction.getTimestamp() != null
                    && transaction.getTimestamp()
                    .toLocalDate()
                    .equals(date)) {

                result.add(transaction);
            }
        }

        return result;
    }

    public List<Transaction> getTransactionsForMonth(
            Account account,
            YearMonth month) {

        List<Transaction> result =
                new ArrayList<>();

        if (account == null || month == null) {
            return result;
        }

        for (Transaction transaction :
                account.getTransactions()) {

            if (transaction.getTimestamp() != null
                    && YearMonth.from(
                    transaction.getTimestamp())
                    .equals(month)) {

                result.add(transaction);
            }
        }

        return result;
    }

    public double getTotalByType(
            Account account,
            String transactionType) {

        if (account == null
                || transactionType == null) {
            return 0;
        }

        double total = 0;

        for (Transaction transaction :
                account.getTransactions()) {

            if (transaction.getType()
                    .equalsIgnoreCase(transactionType)) {

                total += transaction.getAmount();
            }
        }

        return total;
    }
}
