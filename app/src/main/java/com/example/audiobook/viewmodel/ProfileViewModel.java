package com.example.audiobook.viewmodel;

import static android.content.Context.MODE_PRIVATE;
import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.helper.SessionManager;
import com.example.audiobook.repository.UserRepository;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<UserResponse> updateResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> username = new MutableLiveData<>();
    private MutableLiveData<String> displayName = new MutableLiveData<>();
    private MutableLiveData<String> dateOfBirth = new MutableLiveData<>();
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getDisplayName() {
        return displayName;
    }

    public LiveData<String> getDateOfBirth() {
        return dateOfBirth;
    }

    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        sessionManager = new SessionManager(application);
    }

    public LiveData<UserResponse> getUpdateResult() {
        return updateResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void updateUserProfile(String userId ,  String username, String displayName, String dateOfBirth, File avatarFile) {
        DateTimeFormatter inFmt  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dateOfBirth, inFmt);
        String formattedDate = ld.format(outFmt);
        // Create RequestBody instances
        RequestBody usernameBody = null;
        RequestBody displayNameBody = null;
        RequestBody dateOfBirthBody = null;
        MultipartBody.Part filePart = null;

        // Only create RequestBody for non-null and non-empty values
        if (username != null && !username.isEmpty()) {
            usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        }
        if (displayName != null && !displayName.isEmpty()) {
            displayNameBody = RequestBody.create(MediaType.parse("text/plain"), displayName);
        }
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            dateOfBirthBody = RequestBody.create(MediaType.parse("text/plain"), formattedDate);
        }
        if (avatarFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), avatarFile);
            filePart = MultipartBody.Part.createFormData("file", avatarFile.getName(), requestFile);
        }



        Map<String, RequestBody> partMap = new HashMap<>();
        partMap.put("username", usernameBody);
        partMap.put("displayName", displayNameBody);
        partMap.put("dateOfBirth", dateOfBirthBody);


        // Make API call
        userRepository.updateUser(userId, partMap, filePart)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e(TAG, "Update success: " + response.body());

                            updateResult.postValue(response.body());
                            // Update session with new user data if needed
//                            if (response.body().getData() != null) {
//                                sessionManager.saveUser(response.body().getData());
//                            }
                        } else {
                            errorMessage.postValue("Update failed: " + (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        errorMessage.postValue("Network error: " + t.getMessage());
                    }
                });
    }
    public void fetchUserInformation(String userId) {
        userRepository.getUserInfor(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    username.setValue(userResponse.getUsername());
                    displayName.setValue(userResponse.getDisplayName());
                    dateOfBirth.setValue(userResponse.getDateOfBirth());
                    imageUrl.setValue(userResponse.getImageUrl());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                // Handle failure
                errorMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }


}
