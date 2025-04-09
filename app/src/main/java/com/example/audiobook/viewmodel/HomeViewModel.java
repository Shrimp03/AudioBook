// HomeViewModel.java
package com.example.audiobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import com.example.audiobook.repository.AudiobookRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final AudiobookRepository repository = new AudiobookRepository();

    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public LiveData<List<Category>> categories = _categories;

    private final MutableLiveData<List<Audiobook>> _recommendedAudiobooks = new MutableLiveData<>();
    public LiveData<List<Audiobook>> recommendedAudiobooks = _recommendedAudiobooks;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void fetchCategories() {
        repository.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _categories.setValue(response.body());
                } else {
                    _error.setValue("Failed to load categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }

    public void fetchRecommendedAudiobooks() {
        repository.getAllAudioBooks().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _recommendedAudiobooks.setValue(response.body().getData().getContent());
                } else {
                    _error.setValue("Failed to load audiobooks.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }
}
