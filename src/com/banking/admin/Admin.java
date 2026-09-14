package com.banking.admin;

import java.io.Serializable;

public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public Admin(
            String username,
            String password) {

        this.username = username;
        this.password = password;
    }

    public String getUsername() {

        return username;
    }

    public boolean verifyPassword(
            String enteredPassword) {

        if (enteredPassword == null) {
            return false;
        }

        return password.equals(enteredPassword);
    }

    public boolean isValidUsername(
            String enteredUsername) {

        if (enteredUsername == null) {
            return false;
        }

        return username.equals(enteredUsername);
    }
}
