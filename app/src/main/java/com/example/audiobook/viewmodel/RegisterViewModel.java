package com.example.audiobook.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.RegisterDTO;
import com.example.audiobook.dto.response.RegisterResponse;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    public MutableLiveData<String> toast = new MutableLiveData<>();
    private static final String TAG = "RegisterViewModel";
    public MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();

    public void registerUser(String email, String password, String dateOfBirth){
        RegisterDTO registerDTO = new RegisterDTO(email,password,dateOfBirth);

        CoreAppInterface.coreAppInterface.registerAccount(registerDTO).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                System.out.println(response);
                Log.d(TAG, "onResponse called."+ response);
                Log.d(TAG, "Response raw: " + response.toString());
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());
                try {
                    if (response.isSuccessful()) {
                        RegisterResponse responseData = response.body();
                        Log.d(TAG, "Response body: " + responseData.toString());
                        if (Objects.equals(responseData.getStatus(), "ok")) {
                            toast.setValue(responseData.getMessage());
                            registrationSuccess.setValue(true);
                        } else if (response.body() != null && response.code() == 226) {
                        }
                    }
                } catch (Exception e) {

                }
            }
            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                toast.setValue("false");
                registrationSuccess.setValue(false);

            }
        });
    }
}
