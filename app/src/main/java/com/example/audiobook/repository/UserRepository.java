package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import androidx.lifecycle.MutableLiveData;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.helper.SessionManager;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {

    private CoreAppInterface coreAppInterface;

    public UserRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }

    public Call<ResponseObject<UserResponse>> registerAccount(RegisterRequest registerRequest) {
        return coreAppInterface.registerAccount(registerRequest);
    };
}