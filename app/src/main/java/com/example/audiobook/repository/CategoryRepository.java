package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.dto.response.ResponseObject;

import java.util.List;
import java.util.Map;

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

    public Call<ResponseObject> addRecommendCategory(Map<String, Object> requestBody){
        return coreAppInterface.addRecommendCategory(requestBody);
    }

    public Call<ResponseObject> checkFirstLogin(){
        return coreAppInterface.checkFirstLogin();
    }
}