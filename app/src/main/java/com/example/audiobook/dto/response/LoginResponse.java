package com.example.audiobook.dto.response;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private String accessToken;

    private String refreshToken;

    private boolean authenticated;

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}
