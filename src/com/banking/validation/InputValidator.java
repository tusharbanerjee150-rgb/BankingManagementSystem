package com.banking.validation;

public final class InputValidator {

    private InputValidator() {
    }

    public static boolean isValidName(String name) {

        if (name == null) {
            return false;
        }

        String value = name.trim();

        return !value.isEmpty()
                && value.length() <= 100
                && value.matches("[A-Za-z ]+");
    }

    public static boolean isValidPhone(String phone) {

        if (phone == null) {
            return false;
        }

        return phone.matches("[6-9][0-9]{9}");
    }

    public static boolean isValidEmail(String email) {

        if (email == null) {
            return false;
        }

        String value = email.trim();

        return value.length() <= 150
                && value.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    public static boolean isValidPin(String pin) {

        return pin != null
                && pin.matches("[0-9]{4}");
    }

    public static boolean isValidAmount(double amount) {

        return !Double.isNaN(amount)
                && !Double.isInfinite(amount)
                && amount > 0;
    }

    public static boolean isValidAccountNumber(
            String accountNumber) {

        if (accountNumber == null) {
            return false;
        }

        return accountNumber.matches("[A-Za-z0-9]{6,20}");
    }

    public static boolean isValidCustomerId(
            String customerId) {

        if (customerId == null) {
            return false;
        }

        return customerId.matches("[A-Za-z0-9]{4,20}");
    }

    public static boolean isValidAddress(
            String address) {

        if (address == null) {
            return false;
        }

        String value = address.trim();

        return !value.isEmpty()
                && value.length() <= 250;
    }

    public static boolean isValidAccountType(
            String accountType) {

        if (accountType == null) {
            return false;
        }

        return accountType.equalsIgnoreCase("savings")
                || accountType.equalsIgnoreCase("current");
    }

    public static boolean isValidUsername(
            String username) {

        if (username == null) {
            return false;
        }

        String value = username.trim();

        return !value.isEmpty()
                && value.length() <= 50
                && value.matches("[A-Za-z0-9._-]+");
    }
}
