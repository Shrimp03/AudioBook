package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudiobookRepository {
    private CoreAppInterface coreAppInterface;

    public AudiobookRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }

    public Call<List<Category>> getAllCategories() {
        return coreAppInterface.getAllCategory();
    }
    public Call<ApiResponse> getAllAudioBooks(){return coreAppInterface.getAllAudioBooks();};
}