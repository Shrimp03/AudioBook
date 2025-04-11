package com.example.audiobook.models;

public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;

    // Getters and setters
    public T getData() { return data; }
}
