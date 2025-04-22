package com.example.audiobook.dto.response;

public class ResponseObject<T> {

    private String status;

    private String message;

    private T data;

    // Getters and setters
    public T getData() {
        return this.data;
    }

    public String getStatus() {
        return this.status;
    }

    public String getMessage() {
        return message;
    }
}
