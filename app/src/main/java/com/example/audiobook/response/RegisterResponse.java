package com.example.audiobook.response;

import java.io.Serializable;

public class RegisterResponse implements Serializable {
    private String status;
    private String message;

    public RegisterResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
    public RegisterResponse(){

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

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
