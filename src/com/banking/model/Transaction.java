package com.banking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int transactionCounter = 1000;

    private String transactionId;
    private String type;
    private double amount;
    private String description;
    private LocalDateTime dateTime;

    public Transaction(String type, double amount, String description) {

        transactionCounter++;

        this.transactionId = String.format("TXN%04d", transactionCounter);
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public static void initializeCounter(int highestTransactionId) {

        if (highestTransactionId > transactionCounter) {
            transactionCounter = highestTransactionId;
        }
    }

    public static int getCounter() {
        return transactionCounter;
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        return String.format(
                "%s | %-16s | Amount: %.2f | %s | %s",
                transactionId,
                type,
                amount,
                description,
                dateTime.format(formatter)
        );
    }
}