package com.example.audiobook.dto.response;

import java.util.UUID;

public class AudioBookResponse {

    private UUID id;

    private String author;

    private String coverImage;

    private String description;

    private int duration;

    private String femaleAudioUrl;

    private String femaleAudioUrlFull;

    private boolean isFree;

    private String maleAudioUrl;

    private String maleAudioUrlFull;

    private int publishedYear;

    private String title;

    private String textContent;

    private CategoryResponse categoryResponse;

    private UUID userId;

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    // Constructor
    public AudioBookResponse() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFemaleAudioUrl() {
        return femaleAudioUrl;
    }

    public void setFemaleAudioUrl(String femaleAudioUrl) {
        this.femaleAudioUrl = femaleAudioUrl;
    }

    public String getFemaleAudioUrlFull() {
        return femaleAudioUrlFull;
    }

    public void setFemaleAudioUrlFull(String femaleAudioUrlFull) {
        this.femaleAudioUrlFull = femaleAudioUrlFull;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public String getMaleAudioUrl() {
        return maleAudioUrl;
    }

    public void setMaleAudioUrl(String maleAudioUrl) {
        this.maleAudioUrl = maleAudioUrl;
    }

    public String getMaleAudioUrlFull() {
        return maleAudioUrlFull;
    }

    public void setMaleAudioUrlFull(String maleAudioUrlFull) {
        this.maleAudioUrlFull = maleAudioUrlFull;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CategoryResponse getCategoryResponse() {
        return categoryResponse;
    }

    public void setCategoryResponse(CategoryResponse categoryResponse) {
        this.categoryResponse = categoryResponse;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AudioBook{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", femaleAudioUrl='" + femaleAudioUrl + '\'' +
                ", femaleAudioUrlFull='" + femaleAudioUrlFull + '\'' +
                ", isFree=" + isFree +
                ", maleAudioUrl='" + maleAudioUrl + '\'' +
                ", maleAudioUrlFull='" + maleAudioUrlFull + '\'' +
                ", publishedYear=" + publishedYear +
                ", title='" + title + '\'' +
                ", categoryId=" + categoryResponse.getId() +
                ", userId=" + userId +
                '}';
    }
}