package com.banking.model;

import com.banking.validation.InputValidator;

public class CurrentAccount extends Account {

    private static final long serialVersionUID = 1L;

    private static final double OVERDRAFT_LIMIT = 10000.00;

    public CurrentAccount(
            String accountNumber,
            Customer customer,
            String pin,
            double balance) {

        super(
                accountNumber,
                customer,
                pin,
                balance
        );
    }

    @Override
    public boolean withdraw(double amount) {

        if (!active
                || frozen
                || !InputValidator.isValidAmount(amount)
                || amount > balance + OVERDRAFT_LIMIT) {
            return false;
        }

        balance -= amount;

        addTransaction(
                "WITHDRAWAL",
                amount,
                "Cash withdrawn from current account"
        );

        return true;
    }

    public double getOverdraftLimit() {
        return OVERDRAFT_LIMIT;
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }
}
