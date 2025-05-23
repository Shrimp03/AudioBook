package com.example.audiobook.api;

public class APIconst {
    public static final String BASE_URL = "http://172.11.50.93:8080";

    // Authentication
    public static final String LOGIN = BASE_URL + "/api/auth/token";

    public static final String UPDATED_FCM_TOKEN = BASE_URL + "/api/user/fcm-token";

    // User
    public static final String USER_REGISTER = BASE_URL + "/api/user/register";
    public static final String UPDATE_USER = BASE_URL + "/api/user/update";

    public static final String USER_INFOR = BASE_URL + "/api/user";

    // Category
    public static final String GET_CATEGORIES = BASE_URL + "/api/category/";
    public static final String ADD_RECOMMEND_CATEGORIES = BASE_URL + "/api/favorite-categories/add";
    public static final String CHECK_FIRST_LOGIN = BASE_URL + "/api/favorite-categories/exists";

    // Audio Book
    public static final String GET_AUDIO_BOOKS = BASE_URL + "/api/audio-book/";
    public static final String GET_RECOMMEND = BASE_URL + "/api/audio-book/recommend";
    public static final String GET_NEW_RELEASE = BASE_URL + "/api/audio-book/new-release";
    public static final String GET_AUDIO_BOOKS_BY_TITLE = BASE_URL + "/api/audio-book/title";
    public static final String GET_AUDIO_BOOKS_BY_CATEGORY_ID = BASE_URL + "/api/audio-book/category/{categoryId}";
    public static final String GET_AUDIO_BOOKS_BY_USER_ID = BASE_URL + "/api/audio-book/user";
    public static final String GET_AUDIO_BOOK_BY_ID = BASE_URL + "/api/audio-book/{audioBookId}";
    public static final String GET_AUDIO_BOOK_BY_SEARCH = BASE_URL + "/api/audio-book/search";
    public static final String GET_AUDIO_BOOK_BY_SEARCH_WITH_USER = BASE_URL + "/api/audio-book/search-by-user";
    public static final String CREATE_AUDIO_BOOK = BASE_URL + "/api/audio-book/create";
    public static final String UPDATE_AUDIO_BOOK = BASE_URL + "/api/audio-book/update/{id}";
    public static final String DELETE_AUDIO_BOOK = BASE_URL + "/api/audio-book/delete/{id}";

    // Rating
    public static final String GET_RATINGS = BASE_URL + "/api/rating/";
    public static final String SUBMIT_RATING = BASE_URL + "/api/rating/";

    // File
    public static final String UPLOAD_IMAGES_AUDIO_BOOK = BASE_URL + "/api/files/cover-image";
    public static final String UPLOAD_AUDIO_AUDIO_BOOK = BASE_URL + "/api/files/audio";

}
