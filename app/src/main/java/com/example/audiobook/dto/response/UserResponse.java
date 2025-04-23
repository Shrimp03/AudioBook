package com.example.audiobook.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class UserResponse {
    private String id;

    private String userName;

    private String email;

    private String role;

    private String password;

    private String dateOfBirth;

    private Instant preniumExpiry;

    private boolean premiumStatus;

    public void setId(String id) {
        this.id = id;
    }

}
