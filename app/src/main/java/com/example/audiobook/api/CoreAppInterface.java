package com.example.audiobook.api;

import com.example.audiobook.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoreAppInterface {

    @GET(APIconst.GET_ALL_CATE)
    Call<List<Category>> getAllCategory();
}
