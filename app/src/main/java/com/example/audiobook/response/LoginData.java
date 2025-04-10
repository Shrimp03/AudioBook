package com.example.audiobook.response;

import java.io.Serializable;

public class LoginData implements Serializable {
    private String accessToken;

    private String refreshToken;

    private boolean authenticated;

    public LoginData(String accessToken, String refreshToken, boolean authenticated) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authenticated = authenticated;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
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
