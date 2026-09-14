package com.banking.model;

import java.io.Serializable;

public class Beneficiary implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String beneficiaryName;
    private String accountType;

    public Beneficiary(
            String accountNumber,
            String beneficiaryName,
            String accountType) {

        this.accountNumber = accountNumber;
        this.beneficiaryName = beneficiaryName;
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getAccountType() {
        return accountType;
    }

    @Override
    public String toString() {

        return "Account Number: " + accountNumber
                + " | Name: " + beneficiaryName
                + " | Account Type: " + accountType;
    }
}