package com.banking.service;

import com.banking.admin.Admin;
import com.banking.model.Account;

public class AuthenticationService {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public Account authenticateCustomer(
            Bank bank,
            String accountNumber,
            String pin) {

        if (bank == null
                || accountNumber == null
                || pin == null) {
            return null;
        }

        Account account =
                bank.findAccount(accountNumber);

        if (account == null
                || !account.isActive()
                || account.isFrozen()) {
            return null;
        }

        if (!account.verifyPin(pin)) {
            return null;
        }

        return account;
    }

    public Admin authenticateAdmin(
            Bank bank,
            String username,
            String password) {

        if (bank == null
                || username == null
                || password == null) {
            return null;
        }

        Admin admin =
                bank.getAdmin();

        if (admin == null) {
            return null;
        }

        if (!admin.getUsername().equals(username)) {
            return null;
        }

        if (!admin.verifyPassword(password)) {
            return null;
        }

        return admin;
    }

    public int getMaxLoginAttempts() {

        return MAX_LOGIN_ATTEMPTS;
    }

    public boolean hasAttemptsRemaining(
            int attemptsUsed) {

        return attemptsUsed < MAX_LOGIN_ATTEMPTS;
    }

    public int getRemainingAttempts(
            int attemptsUsed) {

        int remaining =
                MAX_LOGIN_ATTEMPTS - attemptsUsed;

        return Math.max(remaining, 0);
    }
}
