package com.example.audiobook.dto.request;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public class AudioBookCreateRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("publishedYear")
    private Integer publishedYear;

    @SerializedName("description")
    private String description;

    @SerializedName("coverImage")
    private String coverImage;

    @SerializedName("isFree")
    private Boolean isFree;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("femaleAudioUrl")
    private String femaleAudioUrl;

    @SerializedName("maleAudioUrl")
    private String maleAudioUrl;

    @SerializedName("categoryId")
    private UUID categoryId;

    @SerializedName("textContent")
    public String textContent;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFemaleAudioUrl() {
        return femaleAudioUrl;
    }

    public void setFemaleAudioUrl(String femaleAudioUrl) {
        this.femaleAudioUrl = femaleAudioUrl;
    }

    public String getMaleAudioUrl() {
        return maleAudioUrl;
    }

    public void setMaleAudioUrl(String maleAudioUrl) {
        this.maleAudioUrl = maleAudioUrl;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }


    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}

