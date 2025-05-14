package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.request.RatingRequest;
import com.example.audiobook.dto.response.LoginResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.RatingResponse;
import com.example.audiobook.dto.response.ResponseObject;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RatingRepository {

    private CoreAppInterface coreAppInterface;

    public RatingRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }

    public Call<ResponseObject<PageResponse<RatingResponse>>> getRatings(int page, int size, UUID audioBookId) {
        return coreAppInterface.getRatings(page, size, audioBookId);
    };

    public Call<ResponseObject<String>> submitRating(String token, RatingRequest request) {
        return coreAppInterface.submitRating("Bearer " + token, request);
    }

}