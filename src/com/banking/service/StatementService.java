package com.banking.service;

import com.banking.model.Account;
import com.banking.model.Transaction;

public class StatementService {

    public void generateStatement(Account account) {

        if (account == null) {
            System.out.println(
                    "\nUnable to generate statement."
            );
            return;
        }

        double totalDeposited = 0;
        double totalWithdrawn = 0;
        double totalTransferred = 0;
        double totalReceived = 0;

        for (Transaction transaction :
                account.getTransactions()) {

            String type = transaction.getType();
            double amount = transaction.getAmount();

            if ("DEPOSIT".equalsIgnoreCase(type)) {

                totalDeposited += amount;

            } else if ("WITHDRAW".equalsIgnoreCase(type)) {

                totalWithdrawn += amount;

            } else if ("TRANSFER SENT".equalsIgnoreCase(type)) {

                totalTransferred += amount;

            } else if ("TRANSFER RECEIVED"
                    .equalsIgnoreCase(type)) {

                totalReceived += amount;
            }
        }

        System.out.println(
                "\n=========================================="
        );
        System.out.println(
                "            ACCOUNT STATEMENT"
        );
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Account Number : "
                        + account.getAccountNumber()
        );

        System.out.println(
                "Customer ID    : "
                        + account.getCustomer().getCustomerId()
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
                "Current Balance: %.2f%n",
                account.getBalance()
        );

        System.out.println(
                "Status         : "
                        + account.getStatus()
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Total Transactions : "
                        + account.getTransactions().size()
        );

        System.out.printf(
                "Total Deposited    : %.2f%n",
                totalDeposited
        );

        System.out.printf(
                "Total Withdrawn    : %.2f%n",
                totalWithdrawn
        );

        System.out.printf(
                "Total Transferred  : %.2f%n",
                totalTransferred
        );

        System.out.printf(
                "Total Received     : %.2f%n",
                totalReceived
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "TRANSACTION DETAILS"
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
                "=========================================="
        );
    }

    public void filterTransactions(
            Account account,
            String transactionType) {

        if (account == null) {

            System.out.println(
                    "\nUnable to display transactions."
            );

            return;
        }

        if (transactionType == null
                || transactionType.trim().isEmpty()) {

            System.out.println(
                    "\nInvalid transaction filter."
            );

            return;
        }

        System.out.println(
                "\n========== FILTERED TRANSACTIONS =========="
        );

        System.out.println(
                "Account Number : "
                        + account.getAccountNumber()
        );

        System.out.println(
                "Filter         : "
                        + transactionType
        );

        System.out.println(
                "-------------------------------------------"
        );

        boolean found = false;

        for (Transaction transaction :
                account.getTransactions()) {

            if ("ALL".equalsIgnoreCase(transactionType)
                    || transaction.getType()
                    .equalsIgnoreCase(transactionType)) {

                System.out.println(transaction);

                found = true;
            }
        }

        if (!found) {

            System.out.println(
                    "No matching transactions found."
            );
        }

        System.out.println(
                "==========================================="
        );
    }
}
