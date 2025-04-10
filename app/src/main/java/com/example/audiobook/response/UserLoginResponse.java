package com.example.audiobook.response;

import java.io.Serializable;

public class UserLoginResponse implements Serializable {
    private String status;
    private String message;

    private LoginData data;

    public UserLoginResponse(String status, String message, LoginData data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public UserLoginResponse(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserLoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
