package com.example.audiobook.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.repository.AuthenticationRepository;
import com.example.audiobook.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ViewModel for handling user registration logic
public class RegisterViewModel extends AndroidViewModel {

    // Log tag for debugging
    private static final String TAG = "RegisterViewModel";

    // Repository for authentication operations
    private final UserRepository repository;

    // LiveData for toast messages
    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    // Constructor initializing repository and SharedPreferences
    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository();
    }

    // Initiates user registration with the provided request
    public void registerUser(RegisterRequest request) {
        repository.registerAccount(request).enqueue(new Callback<ResponseObject<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<UserResponse>> call, @NonNull Response<ResponseObject<UserResponse>> response) {
                Log.d(TAG, "Registration response: " + response.body());
                handleRegisterResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<UserResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Registration failed: " + t.getMessage());
                _toastMessage.setValue("Network error. Please try again.");
            }
        });
    }

    // Processes the registration API response and updates LiveData
    private void handleRegisterResponse(Response<ResponseObject<UserResponse>> response) {
        ResponseObject<UserResponse> responseData = validateResponse(response);
        if (responseData == null) return;

        _toastMessage.setValue(responseData.getMessage());
    }

    // Validates the API response and logs details
    private ResponseObject<UserResponse> validateResponse(Response<ResponseObject<UserResponse>> response) {
        logResponse(response);
        if (!response.isSuccessful()) {
            Log.e(TAG, "Unsuccessful response: " + response.code());
            _toastMessage.setValue("Server error. Please try again.");
            return null;
        }

        ResponseObject<UserResponse> responseData = response.body();
        if (responseData == null || !"ok".equals(responseData.getStatus())) {
            Log.e(TAG, "Invalid response data or status: " + (responseData != null ? responseData.getStatus() : "null"));
            _toastMessage.setValue(responseData != null ? responseData.getMessage() : "Registration failed.");
            return null;
        }

        return responseData;
    }

    // Logs response details for debugging
    private void logResponse(Response<?> response) {
        Log.d(TAG, "Response: code=" + response.code() + ", successful=" + response.isSuccessful());
    }

}