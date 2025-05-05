package com.example.audiobook.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.response.LoginResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.AuthenticationRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";
    public static final String PREFS_NAME = "AuthPrefs";
    public static final String TOKEN_KEY = "auth_token";

    private final AuthenticationRepository repository;
    private final SharedPreferences preferences;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthenticationRepository();
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Initiates user login with the provided credentials.
    public void loginUser(LoginRequest request) {
        repository.loginAccount(request).enqueue(new Callback<ResponseObject<LoginResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<LoginResponse>> call, @NonNull Response<ResponseObject<LoginResponse>> response) {
                handleLoginResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<LoginResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Login failed: " + t.getMessage());
                _toastMessage.setValue("Network error. Please try again.");
            }
        });
    }

    // Processes the login API response and updates LiveData.
    private void handleLoginResponse(Response<ResponseObject<LoginResponse>> response) {
        ResponseObject<LoginResponse> responseData = validateResponse(response);
        if (responseData == null) return;

        String token = responseData.getData().getAccessToken();
        if (!isValidToken(token)) {
            Log.e(TAG, "Invalid or missing token.");
            _toastMessage.setValue("Login failed: Invalid token.");
            return;
        }

        saveToken(token);
        _toastMessage.setValue(responseData.getMessage());
    }

    // Validates the API response and logs details.
    private ResponseObject<LoginResponse> validateResponse(Response<ResponseObject<LoginResponse>> response) {
        logResponse(response);
        if (!response.isSuccessful()) {
            Log.e(TAG, "Unsuccessful response: " + response.code());
            _toastMessage.setValue("Server error. Please try again.");
            return null;
        }

        ResponseObject<LoginResponse> responseData = response.body();
        if (responseData == null || !"ok".equals(responseData.getStatus())) {
            Log.e(TAG, "Invalid response data or status: " + (responseData != null ? responseData.getStatus() : "null"));
            _toastMessage.setValue(responseData != null ? responseData.getMessage() : "Login failed.");
            return null;
        }

        return responseData;
    }

    // Logs response details for debugging.
    private void logResponse(Response<?> response) {
        Log.d(TAG, "Response: code=" + response.code() + ", successful=" + response.isSuccessful());
    }

    // Checks if the token is valid (non-null and non-empty).
    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }

    // Saves the authentication token to SharedPreferences.
    private void saveToken(String token) {
        preferences.edit()
                .putString(TOKEN_KEY, token)
                .apply();
        Log.d(TAG, "Token saved successfully.");
    }

    // Retrieves the saved authentication token.
    public String getSavedToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    // Clears the saved authentication token.
    public void clearToken() {
        preferences.edit()
                .remove(TOKEN_KEY)
                .apply();
    }

}