package com.example.audiobook.dto.response;

import java.io.Serializable;

public class RegisterResponse implements Serializable {
    private String status;
    private String message;

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
