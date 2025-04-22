package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryRepository {
    private CoreAppInterface coreAppInterface;

    public CategoryRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        coreAppInterface = retrofit.create(CoreAppInterface.class);
    }

    public Call<List<CategoryResponse>> getAllCategories() {
        return coreAppInterface.getAllCategory();
    }
}