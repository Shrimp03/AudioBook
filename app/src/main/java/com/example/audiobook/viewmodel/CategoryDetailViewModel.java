// CategoryDetailViewModel.java
package com.example.audiobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.AudioBookRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailViewModel extends ViewModel {
    // Repository
    private final AudioBookRepository audioBookRepository = new AudioBookRepository();

    // Live data
    private final MutableLiveData<List<AudioBookResponse>> _audiobooks = new MutableLiveData<>();
    public LiveData<List<AudioBookResponse>> audiobooks = _audiobooks;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void fetchAudiobooksByCategory(String categoryId) {
        audioBookRepository.getAudioBooksByCategory(categoryId).enqueue(new Callback<ResponseObject<PageResponse<AudioBookResponse>>>() {
            @Override
            public void onResponse(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Response<ResponseObject<PageResponse<AudioBookResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _audiobooks.setValue(response.body().getData().getContent());
                } else {
                    _error.setValue("Failed to load audiobooks for category. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }
}