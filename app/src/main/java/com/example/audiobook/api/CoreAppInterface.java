package com.example.audiobook.api;

import com.example.audiobook.dto.LoginDTO;
import com.example.audiobook.dto.RegisterDTO;
import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import com.example.audiobook.response.RegisterResponse;
import com.example.audiobook.response.UserLoginResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoreAppInterface {

    @GET(APIconst.GET_ALL_CATE)
    Call<List<Category>> getAllCategory();

    @POST(APIconst.REGISTER)
    Call<RegisterResponse> registerAccount(@Body RegisterDTO registerDTO);

    @POST(APIconst.LOGIN)
    Call<UserLoginResponse> loginAccount(@Body LoginDTO loginDTO);

    @GET(APIconst.GET_ALL_AUDIO_BOOKS)
    Call<ApiResponse> getAllAudioBooks();

    @GET(APIconst.GET_AUDIO_BOOKS_BY_TITLE)
    Call<List<Audiobook>> getAudioBooksByTitle(@Query("title") String title);

    @GET(APIconst.GET_AUDIO_BOOKS_BY_ID)
    Call<List<Audiobook>> getAudioBooksByCategoryId(@Path("categoryId") String categoryId);

    @GET(APIconst.GET_AUDIO_BOOKS_BY_USER)
    Call<List<Audiobook>> getAudioBooksByUserId(@Path("userId") String userId);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    CoreAppInterface coreAppInterface = new Retrofit.Builder()
            .baseUrl(APIconst.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CoreAppInterface.class);

}
