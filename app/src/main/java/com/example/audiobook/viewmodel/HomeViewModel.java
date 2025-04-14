// HomeViewModel.java
package com.example.audiobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.repository.AudioBookRepository;
import com.example.audiobook.repository.CategoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    // Repository
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final AudioBookRepository audioBookRepository = new AudioBookRepository();

    // Live data
    private final MutableLiveData<List<CategoryResponse>> _categories = new MutableLiveData<>();
    public LiveData<List<CategoryResponse>> categories = _categories;

    private final MutableLiveData<List<AudioBookResponse>> _recommendedAudiobooks = new MutableLiveData<>();
    public LiveData<List<AudioBookResponse>> recommendedAudiobooks = _recommendedAudiobooks;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void fetchCategories() {
        categoryRepository.getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _categories.setValue(response.body());
                } else {
                    _error.setValue("Failed to load categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }

    public void fetchRecommendedAudiobooks() {
        audioBookRepository.getAllAudioBooks().enqueue(new Callback<ResponseObject<PageResponse<AudioBookResponse>>>() {
            @Override
            public void onResponse(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Response<ResponseObject<PageResponse<AudioBookResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _recommendedAudiobooks.setValue((response.body().getData()).getContent());
                } else {
                    _error.setValue("Failed to load audiobooks.");
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }
}
