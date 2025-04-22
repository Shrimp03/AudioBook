package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.LoginResponse;
import com.example.audiobook.dto.response.RegisterResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UserResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationRepository {

    private CoreAppInterface coreAppInterface;

    public AuthenticationRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }

    public Call<ResponseObject<LoginResponse>> loginAccount(LoginRequest loginRequest) {
        return coreAppInterface.loginAccount(loginRequest);
    };

}