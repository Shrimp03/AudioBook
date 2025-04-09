package com.example.audiobook.api;

import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoreAppInterface {

    @GET(APIconst.GET_ALL_CATE)
    Call<List<Category>> getAllCategory();

    @GET(APIconst.GET_ALL_AUDIO_BOOKS)
    Call<ApiResponse> getAllAudioBooks();

    @GET(APIconst.GET_AUDIO_BOOKS_BY_TITLE)
    Call<List<Audiobook>> getAudioBooksByTitle(@Query("title") String title);

    @GET(APIconst.GET_AUDIO_BOOKS_BY_ID)
    Call<List<Audiobook>> getAudioBooksByCategoryId(@Path("categoryId") String categoryId);

    @GET(APIconst.GET_AUDIO_BOOKS_BY_USER)
    Call<List<Audiobook>> getAudioBooksByUserId(@Path("userId") String userId);

}
