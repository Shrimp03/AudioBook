package com.example.audiobook.models;

import java.time.LocalDate;

public class User {
    private  String id, email, password, userName;
    private LocalDate dateOfBirth;

    private boolean premiumStatus;

    public User(String id, String email, String password, String userName, LocalDate dateOfBirth, boolean premiumStatus) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.premiumStatus = premiumStatus;
    }
    public User(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isPremiumStatus() {
        return premiumStatus;
    }

    public void setPremiumStatus(boolean premiumStatus) {
        this.premiumStatus = premiumStatus;
    }
}
