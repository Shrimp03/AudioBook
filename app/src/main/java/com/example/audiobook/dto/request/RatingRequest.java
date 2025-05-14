package com.example.audiobook.dto.request;

public class RatingRequest {
    private String comment;
    private int rating;
    private String audioBookId;
    private String userId;

    public RatingRequest(String comment, int rating, String audioBookId, String userId) {
        this.comment = comment;
        this.rating = rating;
        this.audioBookId = audioBookId;
        this.userId = userId;
    }

    // Getters and setters
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAudioBookId() {
        return audioBookId;
    }

    public void setAudioBookId(String audioBookId) {
        this.audioBookId = audioBookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}