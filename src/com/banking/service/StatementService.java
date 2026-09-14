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

        for (Transaction transaction :
                account.getTransactions()) {

            String type =
                    transaction.getType();

            double amount =
                    transaction.getAmount();

            if ("DEPOSIT".equalsIgnoreCase(type)) {

                totalDeposited += amount;

            } else if ("WITHDRAW".equalsIgnoreCase(type)) {

                totalWithdrawn += amount;

            } else if ("TRANSFER".equalsIgnoreCase(type)) {

                totalTransferred += amount;
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
                        + account.getCustomer()
                        .getCustomerId()
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
                "Current Balance: %.2f%n",
                account.getBalance()
        );

        System.out.println(
                "Status         : "
                        + (account.isActive()
                        ? "ACTIVE"
                        : "CLOSED")
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
}