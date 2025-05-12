package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import com.example.audiobook.api.CoreAppInterface;
import com.example.audiobook.dto.request.AudioBookCreateRequest;
import com.example.audiobook.dto.response.AudioBookCreateResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.UploadResponse;

import okhttp3.MultipartBody;
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
    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getAllAudioBooks(){return coreAppInterface.getAllAudioBooks();};
    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksByCategory(String categoryId){return coreAppInterface.getAudioBooksByCategoryId(categoryId);}
    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getRecommend(String token){
        return coreAppInterface.getRecommend("Bearer " + token);
    }
    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getNewRelease(){
        return coreAppInterface.getNewRelease();
    }
    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksBySearch(String searchTxt){return coreAppInterface.getAudioBooksBySearch(searchTxt);}


    public Call<ResponseObject<AudioBookCreateResponse>> createAudioBook(String token, AudioBookCreateRequest audioBookCreateRequest) {
        return coreAppInterface.createAudioBook("Bearer " + token, audioBookCreateRequest);
    }

    public Call<UploadResponse>uploadImage(MultipartBody.Part file) {
        return coreAppInterface.uploadImages(file);
    }

    public Call<UploadResponse> uploadAudio(MultipartBody.Part file) {
        return coreAppInterface.uploadAudio(file);
    }
}
