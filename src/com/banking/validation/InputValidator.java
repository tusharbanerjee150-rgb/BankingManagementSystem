package com.banking.validation;

import java.util.regex.Pattern;

public class InputValidator {

    private InputValidator() {
        // Prevent object creation
    }

    public static boolean isValidName(String name) {

        return name != null
                && !name.trim().isEmpty()
                && name.matches("[a-zA-Z ]+");
    }

    public static boolean isValidPhone(String phone) {

        return phone != null
                && phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {

        String emailPattern =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return email != null
                && Pattern.matches(emailPattern, email);
    }

    public static boolean isValidPin(String pin) {

        return pin != null
                && pin.matches("\\d{4}");
    }

    public static boolean isValidAmount(double amount) {

        return amount > 0;
    }
}