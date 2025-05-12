package com.example.audiobook.api;

import com.example.audiobook.dto.request.FCMTokenRequest;
import com.example.audiobook.dto.request.AudioBookCreateRequest;
import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.AudioBookCreateResponse;
import com.example.audiobook.dto.response.UploadResponse;
import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.dto.response.LoginResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.PageResponse;
import com.example.audiobook.dto.response.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoreAppInterface {
    // Authentication
    @POST(APIconst.USER_REGISTER)
    Call<ResponseObject<UserResponse>> registerAccount(@Body RegisterRequest registerRequest);

    @POST(APIconst.LOGIN)
    Call<ResponseObject<LoginResponse>> loginAccount(@Body LoginRequest loginRequest);

    @PUT(APIconst.UPDATED_FCM_TOKEN)
    Call<UserResponse> updatedFcmToken(@Body FCMTokenRequest fcmTokenRequest);

    @Multipart
    @PUT(APIconst.UPDATE_USER + "/{userId}")
    Call<UserResponse> updateUser(@Path("userId") String userId,
                                  @PartMap Map<String, RequestBody> partMap,
                                  @Part MultipartBody.Part file );
    @GET(APIconst.USER_INFOR + "/{id}")
    Call<UserResponse> getUserInfor(@Path("id") String id);

    // Category
    @GET(APIconst.GET_CATEGORIES)
    Call<List<CategoryResponse>> getAllCategory();

    @POST(APIconst.ADD_RECOMMEND_CATEGORIES)
    Call<ResponseObject> addRecommendCategory(@Header("Authorization") String authorization, @Body Map<String, Object> requestBod);

    @GET(APIconst.CHECK_FIRST_LOGIN)
    Call<ResponseObject> checkFirstLogin(@Header("Authorization") String authorization);

    // Audio book
    @GET(APIconst.GET_AUDIO_BOOKS)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getAllAudioBooks();

    @GET(APIconst.GET_RECOMMEND)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getRecommend(@Header("Authorization") String authorization);

    @GET(APIconst.GET_NEW_RELEASE)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getNewRelease();

    @GET(APIconst.GET_AUDIO_BOOKS_BY_TITLE)
    Call<List<AudioBookResponse>> getAudioBooksByTitle(@Query("title") String title);

    @GET(APIconst.GET_AUDIO_BOOK_BY_SEARCH)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksBySearch(@Query("searchTxt") String searchTxt);

    @GET(APIconst.GET_AUDIO_BOOK_BY_SEARCH_WITH_USER)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksBySearchWithUser(@Header("Authorization") String authorization, @Query("searchTxt") String searchTxt);
    @GET(APIconst.GET_AUDIO_BOOKS_BY_CATEGORY_ID)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksByCategoryId(@Path("categoryId") String categoryId);

    @GET(APIconst.GET_AUDIO_BOOKS_BY_USER_ID)
    Call<ResponseObject<PageResponse<AudioBookResponse>>> getAudioBooksByUser(@Header("Authorization") String authorization);

    @GET(APIconst.GET_AUDIO_BOOK_BY_ID)
    Call<ResponseObject> getAudioBooksById(@Path("audioBookId") String audioBookId);

    @POST(APIconst.CREATE_AUDIO_BOOK)
    Call<ResponseObject<AudioBookCreateResponse>> createAudioBook(@Header("Authorization") String authorization, @Body AudioBookCreateRequest audioBookCreateRequest);
    @POST(APIconst.UPDATE_AUDIO_BOOK)
    Call<ResponseObject<AudioBookResponse>> updateAudioBook(@Header("Authorization") String authorization, @Body AudioBookCreateRequest audioBookCreateRequest, @Path("id") String id);
    @POST(APIconst.DELETE_AUDIO_BOOK)
    Call<ResponseObject> deleteAudioBook(@Header("Authorization") String authorization, @Path("id") String id);

    @Multipart
    @POST(APIconst.UPLOAD_IMAGES_AUDIO_BOOK)
    Call<UploadResponse>uploadImages(@Part MultipartBody.Part file);

    @Multipart
    @POST(APIconst.UPLOAD_AUDIO_AUDIO_BOOK)
    Call<UploadResponse> uploadAudio(@Part MultipartBody.Part file);

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    CoreAppInterface coreAppInterface = new Retrofit.Builder()
            .baseUrl(APIconst.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CoreAppInterface.class);
}
