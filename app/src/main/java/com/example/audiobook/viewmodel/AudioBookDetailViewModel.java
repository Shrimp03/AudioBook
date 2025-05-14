// SearchViewModel.java
package com.example.audiobook.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.dto.request.RatingRequest;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.RatingResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.AudioBookRepository;
import com.example.audiobook.repository.RatingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioBookDetailViewModel extends AndroidViewModel {
    // Repository
    private final RatingRepository ratingRepository = new RatingRepository();

    // Live data
    private final MutableLiveData<List<RatingResponse>> _ratings = new MutableLiveData<>();
    public LiveData<List<RatingResponse>> ratings = _ratings;

    private final MutableLiveData<String> _ratingResult = new MutableLiveData<>();
    public LiveData<String> ratingResult = _ratingResult;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public AudioBookDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchRatings(UUID audioBookId) {
        ratingRepository.getRatings(1, 3, audioBookId).enqueue(new Callback<ResponseObject<PageResponse<RatingResponse>>>() {
            @Override
            public void onResponse(Call<ResponseObject<PageResponse<RatingResponse>>> call, Response<ResponseObject<PageResponse<RatingResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _ratings.setValue(response.body().getData().getContent());
                } else {
                    _error.setValue("Failed to load audiobooks for category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<PageResponse<RatingResponse>>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }

    public void fetchAllRatings(UUID audioBookId) {
        ratingRepository.getRatings(1, 100, audioBookId).enqueue(new Callback<ResponseObject<PageResponse<RatingResponse>>>() {
            @Override
            public void onResponse(Call<ResponseObject<PageResponse<RatingResponse>>> call, Response<ResponseObject<PageResponse<RatingResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _ratings.setValue(response.body().getData().getContent());
                } else {
                    _error.setValue("Failed to load all ratings. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<PageResponse<RatingResponse>>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }

    public void submitRating(UUID audioBookId, UUID userId, int rating, String comment, String token) {
        RatingRequest request = new RatingRequest(
                comment, rating, audioBookId.toString(), userId.toString()
        );

        ratingRepository.submitRating(token, request).enqueue(new Callback<ResponseObject<String>>() {
            @Override
            public void onResponse(Call<ResponseObject<String>> call, Response<ResponseObject<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _ratingResult.setValue("Rating submitted successfully");
                } else {
                    _ratingResult.setValue("Failed to submit rating. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<String>> call, Throwable t) {
                _ratingResult.setValue("API error: " + t.getMessage());
            }
        });
    }
}