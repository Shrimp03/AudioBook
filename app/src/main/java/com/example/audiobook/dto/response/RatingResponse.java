package com.example.audiobook.dto.response;

import java.util.UUID;

public class RatingResponse {
    private UUID id;

    private String comment;

    private String createdAt;

    private Integer rating;

    private UserResponse userResponse;

    public UUID getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getRating() {
        return rating;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }
}
