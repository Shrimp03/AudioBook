// SearchViewModel.java
package com.example.audiobook.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.AudioBookRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SearchViewModel";
    private final AudioBookRepository audioBookRepository = new AudioBookRepository();

    private final MutableLiveData<List<AudioBookResponse>> _searchResults = new MutableLiveData<>();
    public LiveData<List<AudioBookResponse>> searchResults = _searchResults;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public void searchAudiobooks(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                Log.d(TAG, "Search query is empty, clearing results");
                _searchResults.setValue(new ArrayList<>());
                return;
            }

            Log.d(TAG, "Initiating search for query: " + query);
            Call<ResponseObject<PageResponse<AudioBookResponse>>> call = audioBookRepository.getAudioBooksBySearch(query);
            if (call == null) {
                Log.e(TAG, "Repository returned null Call object");
                _error.setValue("Lỗi: Không thể gọi API");
                return;
            }

            call.enqueue(new Callback<ResponseObject<PageResponse<AudioBookResponse>>>() {
                @Override
                public void onResponse(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Response<ResponseObject<PageResponse<AudioBookResponse>>> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<AudioBookResponse> pageResponse = response.body().getData();
                            if (pageResponse != null && pageResponse.getContent() != null) {
                                Log.d(TAG, "Search successful, results: " + pageResponse.getContent().size());
                                _searchResults.setValue(pageResponse.getContent());
                            } else {
                                Log.w(TAG, "Search returned empty data");
                                _searchResults.setValue(new ArrayList<>());
                            }
                        } else {
                            Log.e(TAG, "Search failed with code: " + response.code());
                            _error.setValue("Lỗi tải kết quả tìm kiếm. Mã: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing search response: " + e.getMessage(), e);
                        _error.setValue("Lỗi xử lý dữ liệu: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject<PageResponse<AudioBookResponse>>> call, Throwable t) {
                    Log.e(TAG, "API call failed: " + t.getMessage(), t);
                    _error.setValue("Lỗi API: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in searchAudiobooks: " + e.getMessage(), e);
            _error.setValue("Lỗi tìm kiếm: " + e.getMessage());
        }
    }
}