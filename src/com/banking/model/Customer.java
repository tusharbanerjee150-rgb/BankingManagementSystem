package com.banking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerId;
    private String name;
    private String phone;
    private String email;
    private String address;

    private List<Beneficiary> beneficiaries;

    public Customer(
            String customerId,
            String name,
            String phone,
            String email,
            String address) {

        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;

        this.beneficiaries = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public boolean addBeneficiary(Beneficiary beneficiary) {

        if (beneficiary == null) {
            return false;
        }

        for (Beneficiary existing : beneficiaries) {

            if (existing.getAccountNumber()
                    .equals(beneficiary.getAccountNumber())) {

                return false;
            }
        }

        beneficiaries.add(beneficiary);
        return true;
    }

    public boolean removeBeneficiary(String accountNumber) {

        for (int i = 0; i < beneficiaries.size(); i++) {

            if (beneficiaries.get(i)
                    .getAccountNumber()
                    .equals(accountNumber)) {

                beneficiaries.remove(i);
                return true;
            }
        }

        return false;
    }

    public Beneficiary findBeneficiary(
            String accountNumber) {

        for (Beneficiary beneficiary : beneficiaries) {

            if (beneficiary.getAccountNumber()
                    .equals(accountNumber)) {

                return beneficiary;
            }
        }

        return null;
    }

    public void displayBeneficiaries() {

        System.out.println(
                "\n========== SAVED BENEFICIARIES =========="
        );

        if (beneficiaries.isEmpty()) {

            System.out.println(
                    "No beneficiaries added."
            );

        } else {

            for (int i = 0;
                 i < beneficiaries.size();
                 i++) {

                System.out.println(
                        (i + 1) + ". "
                                + beneficiaries.get(i)
                );
            }
        }

        System.out.println(
                "========================================="
        );
    }

    public void displayProfile() {

        System.out.println(
                "\n========== CUSTOMER PROFILE =========="
        );

        System.out.println(
                "Customer ID : " + customerId
        );

        System.out.println(
                "Name        : " + name
        );

        System.out.println(
                "Phone       : " + phone
        );

        System.out.println(
                "Email       : " + email
        );

        System.out.println(
                "Address     : " + address
        );

        System.out.println(
                "======================================"
        );
    }
}