package com.banking.model;

import com.banking.validation.InputValidator;

public class SavingsAccount extends Account {

    private static final long serialVersionUID = 1L;

    private static final double INTEREST_RATE = 4.0;

    public SavingsAccount(
            String accountNumber,
            Customer customer,
            String pin,
            double balance) {

        super(accountNumber, customer, pin, balance);
    }

    @Override
    public boolean withdraw(double amount) {

        if (!active || !InputValidator.isValidAmount(amount)) {
            return false;
        }

        if (amount > balance) {
            return false;
        }

        balance -= amount;

        addTransaction(
                "WITHDRAW",
                amount,
                "Cash withdrawn from savings account"
        );

        return true;
    }

    public double calculateInterest() {

        return balance * INTEREST_RATE / 100;
    }

    public double getInterestRate() {

        return INTEREST_RATE;
    }

    @Override
    public String getAccountType() {

        return "Savings Account";
    }
}