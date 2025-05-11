package com.example.audiobook.dto.request;

public class FCMTokenRequest {
    private String userId;
    private String fcmToken;
    public FCMTokenRequest(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }
    public String getUserId() {
        return userId;
    }
    public String getFcmToken() {
        return fcmToken;
    }
}