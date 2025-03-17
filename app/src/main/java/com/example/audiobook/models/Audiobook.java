package com.example.audiobook.models;

public class Audiobook {
    private String title;
    private String filePath;

    public Audiobook(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }
}