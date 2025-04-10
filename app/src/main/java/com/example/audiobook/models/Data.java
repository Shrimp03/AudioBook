package com.example.audiobook.models;

import java.util.List;

public class Data {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
    private List<Audiobook> content;

    // Getters and setters
    public List<Audiobook> getContent() { return content; }
}