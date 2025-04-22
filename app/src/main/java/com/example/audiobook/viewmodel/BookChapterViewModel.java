// HomeViewModel.java
package com.example.audiobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.BookChapterResponse;
import com.example.audiobook.repository.BookChapterRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookChapterViewModel extends ViewModel {
    // Repository
    private final BookChapterRepository bookChapterRepository = new BookChapterRepository();

    // Live data
    private final MutableLiveData<List<BookChapterResponse>> _bookChapters = new MutableLiveData<>();
    public LiveData<List<BookChapterResponse>> bookChapters = _bookChapters;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void fetchBookChaptersByAudioBookId(String audioBookId) {
        bookChapterRepository.getAllByAudioBookId(audioBookId).enqueue(new Callback<ResponseObject<List<BookChapterResponse>>>() {
            @Override
            public void onResponse(Call<ResponseObject<List<BookChapterResponse>>> call, Response<ResponseObject<List<BookChapterResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _bookChapters.setValue(response.body().getData());
                } else {
                    _error.setValue("Failed to load book chapters. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseObject<List<BookChapterResponse>>> call, Throwable t) {
                _error.setValue("API error: " + t.getMessage());
            }
        });
    }
}
