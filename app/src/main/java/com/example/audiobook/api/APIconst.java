package com.example.audiobook.api;

public class APIconst {
    public static final String BASE_URL = "http://192.168.31.88:8080";

    // Authentication
    public static final String LOGIN = BASE_URL + "/api/auth/token";

    // User
    public static final String USER_REGISTER = BASE_URL + "/api/user/register";

    // Category
    public static final String GET_CATEGORIES = BASE_URL + "/api/category/";
    public static final String ADD_RECOMMEND_CATEGORIES = BASE_URL + "/api/favorite-categories/add";
    public static final String CHECK_FIRST_LOGIN = BASE_URL + "/api/favorite-categories/exists";

    // Audio Book
    public static final String GET_AUDIO_BOOKS = BASE_URL + "/api/audio-book/";
    public static final String GET_AUDIO_BOOKS_BY_TITLE = BASE_URL + "/api/audio-book/title";
    public static final String GET_AUDIO_BOOKS_BY_CATEGORY_ID = BASE_URL + "/api/audio-book/category/{categoryId}";
    public static final String GET_AUDIO_BOOKS_BY_USER_ID = BASE_URL + "/api/audio-book/user/{userId}";
    public static final String GET_AUDIO_BOOK_BY_ID = BASE_URL + "/api/audio-book/{audioBookId}";

    public static final String GET_AUDIO_BOOK_BY_SEARCH = BASE_URL + "/api/audio-book/search";

    // Book Chapter
    public static final String GET_BOOK_CHAPTERS_BY_AUDIO_BOOK_ID = BASE_URL + "/api/book-chapter/audio-book/{audioBookId}";
}
