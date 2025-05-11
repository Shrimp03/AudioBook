package com.example.audiobook.api;

import com.example.audiobook.dto.request.FCMTokenRequest;
import com.example.audiobook.dto.response.ResponseObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {
    @POST("api/users/fcm-token")
    Call<ResponseObject> updateFCMToken(
        @Header("Authorization") String token,
        @Body FCMTokenRequest request
    );
} 