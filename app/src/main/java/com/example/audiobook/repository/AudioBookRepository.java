package com.example.audiobook.repository;

import static com.example.audiobook.api.APIconst.BASE_URL;

import android.util.Log;

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

    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksByUser(String token) {
        return coreAppInterface.getAudioBooksByUser("Bearer " + token);
    }

    public Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksBySearchWithUser(String token, String searchTxt){
        Log.d("audio repo", token + "   " + searchTxt );

        return coreAppInterface.getAudioBooksBySearchWithUser("Bearer " + token, searchTxt);
    }

    public Call<ResponseObject<AudioBookResponse>> updateAudioBook(String token, AudioBookCreateRequest audioBookCreateRequest, String id) {
        return coreAppInterface.updateAudioBook("Bearer " + token, audioBookCreateRequest, id);
    }

    public Call<ResponseObject> deleteAudioBook(String token,String id) {
        return coreAppInterface.deleteAudioBook("Bearer " + token, id);
    }
}
