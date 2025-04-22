package com.example.audiobook.dto.request;

public class RegisterRequest {
    private String email;
    private String password;
    private String dateOfBirth;

    public RegisterRequest(String email, String password, String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

}
