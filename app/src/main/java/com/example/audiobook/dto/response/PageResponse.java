package com.example.audiobook.dto.response;

import java.util.List;

public class PageResponse<T> {

    private int page;

    private int size;

    private int totalElements;

    private int totalPages;

    private List<T> content;

    // Getters and setters
    public List<T> getContent() { return content; }
}