package com.example.audiobook.dto.response;

import com.google.gson.annotations.SerializedName;

public class AudioBookCreateResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AudioBookData data;

    // Getters and setters
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

    public AudioBookData getData() {
        return data;
    }

    public void setData(AudioBookData data) {
        this.data = data;
    }
}

