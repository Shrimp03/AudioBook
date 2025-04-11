package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import com.example.audiobook.models.PageResponse;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudioBookRepository {
    private CoreAppInterface coreAppInterface;

    public AudioBookRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }
    public Call<ApiResponse<PageResponse<Audiobook>>> getAllAudioBooks(){return coreAppInterface.getAllAudioBooks();};

//    public Call<ApiResponse> getAudioBook(UUID audioBookId) {
//        return coreAppInterface.getAudioBooksById(audioBookId);
//    }
}