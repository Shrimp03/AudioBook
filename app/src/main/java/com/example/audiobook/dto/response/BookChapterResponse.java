package com.example.audiobook.dto.response;

import java.util.UUID;

public class BookChapterResponse {

    UUID id;

    String title;

    Integer chapterNumber;

    String textContent;


    public String getTitle() {
        return title;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }
}