package com.example.audiobook.api;

public class APIconst {
    public static final String BASE_URL = "http://172.11.126.17:8080";

    // Authentication
    public static final String REGISTER = BASE_URL + "/user/register";
    public static final String LOGIN = BASE_URL + "/auth/token/get";

    // Category
    public static final String GET_ALL_CATE = BASE_URL + "/api/category/getAll";

    // Audio Book
    public static final String GET_ALL_AUDIO_BOOKS = BASE_URL + "/api/audiobooks/getAll";
    public static final String GET_AUDIO_BOOKS_BY_TITLE = BASE_URL + "/api/audiobooks/title";
    public static final String GET_AUDIO_BOOKS_BY_CATEGORY_ID = BASE_URL + "/api/audiobooks/category/{categoryId}";
    public static final String GET_AUDIO_BOOKS_BY_USER_ID = BASE_URL + "/api/audiobooks/user/{userId}";
    public static final String GET_AUDIO_BOOKS_BY_ID = BASE_URL + "/api/audiobooks/{audioBookId}";
}
