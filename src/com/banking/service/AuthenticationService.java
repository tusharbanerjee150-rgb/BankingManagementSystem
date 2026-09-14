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

        if (account == null) {

            logCustomerFailure(
                    bank,
                    accountNumber,
                    "ACCOUNT NOT FOUND"
            );

            return null;
        }

        if (!account.isActive()) {

            logCustomerFailure(
                    bank,
                    accountNumber,
                    "ACCOUNT CLOSED"
            );

            return null;
        }

        if (account.isFrozen()) {

            logCustomerFailure(
                    bank,
                    accountNumber,
                    "ACCOUNT FROZEN"
            );

            return null;
        }

        if (!account.verifyPin(pin)) {

            logCustomerFailure(
                    bank,
                    accountNumber,
                    "INVALID PIN"
            );

            return null;
        }

        logCustomerSuccess(
                bank,
                accountNumber
        );

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

            logAdminFailure(
                    bank,
                    username,
                    "ADMIN ACCOUNT UNAVAILABLE"
            );

            return null;
        }

        if (!admin.isValidUsername(username)) {

            logAdminFailure(
                    bank,
                    username,
                    "INVALID USERNAME"
            );

            return null;
        }

        if (!admin.verifyPassword(password)) {

            logAdminFailure(
                    bank,
                    username,
                    "INVALID PASSWORD"
            );

            return null;
        }

        logAdminSuccess(
                bank,
                username
        );

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

    private void logCustomerSuccess(
            Bank bank,
            String accountNumber) {

        if (bank != null
                && bank.getAuditLogger() != null) {

            bank.getAuditLogger().log(
                    "CUSTOMER LOGIN SUCCESS | Account: "
                            + accountNumber
            );
        }
    }

    private void logCustomerFailure(
            Bank bank,
            String accountNumber,
            String reason) {

        if (bank != null
                && bank.getAuditLogger() != null) {

            bank.getAuditLogger().log(
                    "CUSTOMER LOGIN FAILED | Account: "
                            + accountNumber
                            + " | Reason: "
                            + reason
            );
        }
    }

    private void logAdminSuccess(
            Bank bank,
            String username) {

        if (bank != null
                && bank.getAuditLogger() != null) {

            bank.getAuditLogger().log(
                    "ADMIN LOGIN SUCCESS | Username: "
                            + username
            );
        }
    }

    private void logAdminFailure(
            Bank bank,
            String username,
            String reason) {

        if (bank != null
                && bank.getAuditLogger() != null) {

            bank.getAuditLogger().log(
                    "ADMIN LOGIN FAILED | Username: "
                            + username
                            + " | Reason: "
                            + reason
            );
        }
    }
}
