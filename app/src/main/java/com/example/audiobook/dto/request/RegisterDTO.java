package com.example.audiobook.dto.request;

public class RegisterDTO {
    private String email;
    private String password;
    private String dateOfBirth;

    public RegisterDTO(String email, String password, String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }
    public RegisterDTO(){
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
