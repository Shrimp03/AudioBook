package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import androidx.lifecycle.MutableLiveData;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.FCMTokenRequest;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.helper.SessionManager;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public Call<UserResponse> updateUser(String userId,Map<String, RequestBody> partMap, MultipartBody.Part file) {
        return coreAppInterface.updateUser(userId, partMap, file);
    }

    public Call<UserResponse> getUserInfor(String id){
        return coreAppInterface.getUserInfor(id);
    }

    public Call<UserResponse> updatedFcmToken(FCMTokenRequest fcmTokenRequest){
        return coreAppInterface.updatedFcmToken(fcmTokenRequest);
    }
}