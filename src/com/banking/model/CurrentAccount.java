package com.banking.model;

import com.banking.validation.InputValidator;

public class CurrentAccount extends Account {

    private static final long serialVersionUID = 1L;

    private static final double OVERDRAFT_LIMIT = 10000.0;

    public CurrentAccount(
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

        if (amount > balance + OVERDRAFT_LIMIT) {
            return false;
        }

        balance -= amount;

        addTransaction(
                "WITHDRAW",
                amount,
                "Cash withdrawn from current account"
        );

        return true;
    }

    public double getOverdraftLimit() {

        return OVERDRAFT_LIMIT;
    }

    public double calculateInterest() {

        return 0;
    }

    @Override
    public String getAccountType() {

        return "Current Account";
    }
}