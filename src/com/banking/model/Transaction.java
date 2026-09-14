package com.banking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static long transactionCounter = 1000;

    private String transactionId;
    private String type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(
            String type,
            double amount,
            String description) {

        transactionCounter++;

        this.transactionId =
                "TXN" + transactionCounter;

        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static long getTransactionCounter() {
        return transactionCounter;
    }

    public static void setTransactionCounter(long counter) {
        transactionCounter = counter;
    }

    public static void initializeCounter(long counter) {
        transactionCounter = counter;
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );

        return String.format(
                "Transaction ID: %s | Type: %s | Amount: %.2f | Description: %s | Date: %s",
                transactionId,
                type,
                amount,
                description,
                timestamp.format(formatter)
        );
    }
}