package com.example.audiobook.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class UserResponse {
    private UUID id;

    private String userName;

    private String email;

    private String role;

    private String password;

    private LocalDate dateOfBirth;

    private Instant preniumExpiry;

    private boolean premiumStatus;

    public void setId(UUID id) {
        this.id = id;
    }

}
