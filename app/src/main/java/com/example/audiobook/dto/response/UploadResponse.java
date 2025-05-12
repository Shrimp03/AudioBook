package com.example.audiobook.dto.response;

public class UploadResponse {

    private String filePath;
    private String dateTime;

    public UploadResponse(String dateTime, String filePath) {
        this.dateTime = dateTime;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
