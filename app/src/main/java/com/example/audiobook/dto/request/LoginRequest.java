package com.example.audiobook.dto.request;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
