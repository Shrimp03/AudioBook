package com.example.audiobook.api;

public class APIconst {
    public static final String BASE_URL = " https://d3ef-14-0-22-145.ngrok-free.app";

    /// uri
    public static final String GET_ALL_CATE = BASE_URL + "/api/category/getAll";
    public static final String GET_ALL_AUDIO_BOOKS = BASE_URL + "/api/audiobooks/getAll";

    public static final String REGISTER = BASE_URL + "api/user/register";

    public static final String LOGIN = BASE_URL + "/auth/token/get";

    public static final String GET_AUDIO_BOOKS_BY_TITLE = BASE_URL + "api/audiobooks/title";
    public static final String GET_AUDIO_BOOKS_BY_ID = BASE_URL + "api/audiobooks/category/{categoryId}";
    public static final String GET_AUDIO_BOOKS_BY_USER = BASE_URL + "api/audiobooks/user/{userId}";
}
