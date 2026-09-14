package com.banking.model;

import com.banking.validation.InputValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String accountNumber;
    protected Customer customer;
    protected String pin;
    protected double balance;
    protected boolean active;

    protected List<Transaction> transactions;

    public Account(
            String accountNumber,
            Customer customer,
            String pin,
            double balance) {

        this.accountNumber = accountNumber;
        this.customer = customer;
        this.pin = pin;
        this.balance = balance;
        this.active = true;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public boolean changePin(String oldPin, String newPin) {

        if (!verifyPin(oldPin)) {
            return false;
        }

        if (!InputValidator.isValidPin(newPin)) {
            return false;
        }

        if (oldPin.equals(newPin)) {
            return false;
        }

        pin = newPin;

        addTransaction(
                "PIN CHANGE",
                0,
                "Account PIN changed successfully"
        );

        return true;
    }

    public boolean deposit(double amount) {

        if (!active || !InputValidator.isValidAmount(amount)) {
            return false;
        }

        balance += amount;

        addTransaction(
                "DEPOSIT",
                amount,
                "Cash deposited into account"
        );

        return true;
    }

    public boolean transferOut(double amount) {

        if (!active || !InputValidator.isValidAmount(amount)) {
            return false;
        }

        balance -= amount;

        return true;
    }

    public boolean transferIn(double amount) {

        if (!active || !InputValidator.isValidAmount(amount)) {
            return false;
        }

        balance += amount;

        return true;
    }

    public abstract boolean withdraw(double amount);

    public void addTransaction(
            String type,
            double amount,
            String description) {

        transactions.add(
                new Transaction(
                        type,
                        amount,
                        description
                )
        );
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public boolean closeAccount() {

        if (!active || balance != 0) {
            return false;
        }

        active = false;

        addTransaction(
                "ACCOUNT CLOSED",
                0,
                "Account closed successfully"
        );

        return true;
    }

    public abstract String getAccountType();

    public void displayDetails() {

        System.out.println("\n========== ACCOUNT DETAILS ==========");
        System.out.println(
                "Account Number : " + accountNumber
        );
        System.out.println(
                "Customer ID    : "
                        + customer.getCustomerId()
        );
        System.out.println(
                "Customer Name  : "
                        + customer.getName()
        );
        System.out.println(
                "Phone          : "
                        + customer.getPhone()
        );
        System.out.println(
                "Email          : "
                        + customer.getEmail()
        );
        System.out.println(
                "Address        : "
                        + customer.getAddress()
        );
        System.out.println(
                "Account Type   : "
                        + getAccountType()
        );
        System.out.printf(
                "Balance        : %.2f%n",
                balance
        );
        System.out.println(
                "Status         : "
                        + (active ? "ACTIVE" : "CLOSED")
        );
        System.out.println(
                "====================================="
        );
    }
}