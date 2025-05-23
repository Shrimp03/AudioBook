package com.example.audiobook.dto.response;

public class CategoryResponse {
    private String id;
    private String name;

    public CategoryResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Category{id='" + id + "', name='" + name + "'}";
    }
}
