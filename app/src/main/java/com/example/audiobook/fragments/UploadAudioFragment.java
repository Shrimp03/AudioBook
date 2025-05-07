package com.example.audiobook.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.audiobook.R;
import com.example.audiobook.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class UploadAudioFragment extends Fragment {

    private static final String TAG = "UploadAudioFragment";
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private Button btnSelectAudio, btnGenerateAudio, btnCreateAudiobook;
    private EditText etTitle, etAuthor, etPublishYear, etDescription, etTranscribeResult;
    private ProgressBar progressBar;
    private Uri selectedAudioUri;
    private File audioFile; // Lưu file audio từ API text-to-speech
    private ApiService apiService;
    private ActivityResultLauncher<Intent> audioPickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedAudioUri = result.getData().getData();
                        Toast.makeText(getContext(), "Đã chọn file MP3", Toast.LENGTH_SHORT).show();
                        // Tự động gửi file đến API để lấy văn bản
                        uploadAudio();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_audio, container, false);

        // Ánh xạ các thành phần giao diện
        btnSelectAudio = view.findViewById(R.id.btn_select_audio);
        btnGenerateAudio = view.findViewById(R.id.btn_generate_audio);
        btnCreateAudiobook = view.findViewById(R.id.btn_create_audiobook);
        etTitle = view.findViewById(R.id.et_title);
        etAuthor = view.findViewById(R.id.et_author);
        etPublishYear = view.findViewById(R.id.et_publish_year);
        etDescription = view.findViewById(R.id.et_description);
        etTranscribeResult = view.findViewById(R.id.et_transcribe_result);
        progressBar = view.findViewById(R.id.progress_bar_upload);

        // Cấu hình OkHttp với timeout
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Gắn sự kiện click
        btnSelectAudio.setOnClickListener(v -> openFilePicker());
        btnGenerateAudio.setOnClickListener(v -> {
            String text = etTranscribeResult.getText().toString().trim();
            if (!text.isEmpty()) {
                generateAudio(text);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập hoặc chỉnh sửa nội dung", Toast.LENGTH_SHORT).show();
            }
        });
        btnCreateAudiobook.setOnClickListener(v -> {
            if (validateInputs()) {
                createAudiobook();
            }
        });

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        audioPickerLauncher.launch(intent);
    }

    private void uploadAudio() {
        try {
            String filePath = FileUtils.getPath(getContext(), selectedAudioUri);
            if (filePath == null) {
                Toast.makeText(getContext(), "Không thể lấy đường dẫn file", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(getContext(), "File không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (file.length() > MAX_FILE_SIZE) {
                Toast.makeText(getContext(), "File MP3 quá lớn (giới hạn 20MB)", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Log.d(TAG, "Uploading file: " + file.getName());
            progressBar.setVisibility(View.VISIBLE);
            btnSelectAudio.setEnabled(false);
            btnGenerateAudio.setEnabled(false);
            btnCreateAudiobook.setEnabled(false);

            Call<ApiResponse> call = apiService.uploadAudio(body);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnSelectAudio.setEnabled(true);
                    btnGenerateAudio.setEnabled(true);
                    btnCreateAudiobook.setEnabled(true);

                    Log.d(TAG, "Response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        etTranscribeResult.setText(response.body().getText());
                        Toast.makeText(getContext(), "Đã nhận văn bản từ file MP3", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnSelectAudio.setEnabled(true);
                    btnGenerateAudio.setEnabled(true);
                    btnCreateAudiobook.setEnabled(true);

                    Log.e(TAG, "Connection error: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getClass().getSimpleName() + " - " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnSelectAudio.setEnabled(true);
            btnGenerateAudio.setEnabled(true);
            btnCreateAudiobook.setEnabled(true);

            Log.e(TAG, "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi: " + e.getClass().getSimpleName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void generateAudio(String text) {
        progressBar.setVisibility(View.VISIBLE);
        btnSelectAudio.setEnabled(false);
        btnGenerateAudio.setEnabled(false);
        btnCreateAudiobook.setEnabled(false);

        TextToSpeechRequest request = new TextToSpeechRequest();
        request.setText(text);

        Call<okhttp3.ResponseBody> call = apiService.textToSpeech(request);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                btnSelectAudio.setEnabled(true);
                btnGenerateAudio.setEnabled(true);
                btnCreateAudiobook.setEnabled(true);

                Log.d(TAG, "Text-to-speech response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Lưu file MP3 tạm thời
                        String fileName = "temp_audio_" + System.currentTimeMillis() + ".mp3";
                        audioFile = new File(getContext().getCacheDir(), fileName);
                        FileOutputStream fos = new FileOutputStream(audioFile);
                        fos.write(response.body().bytes());
                        fos.close();

                        Toast.makeText(getContext(), "Đã tạo file âm thanh", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error saving file: " + e.getMessage());
                        Toast.makeText(getContext(), "Lỗi khi lưu file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSelectAudio.setEnabled(true);
                btnGenerateAudio.setEnabled(true);
                btnCreateAudiobook.setEnabled(true);

                Log.e(TAG, "Text-to-speech connection error: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getClass().getSimpleName() + " - " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String publishYear = etPublishYear.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            return false;
        }
        if (author.isEmpty()) {
            etAuthor.setError("Vui lòng nhập tác giả");
            return false;
        }
        if (publishYear.isEmpty()) {
            etPublishYear.setError("Vui lòng nhập năm xuất bản");
            return false;
        }
        if (description.isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả");
            return false;
        }
        if (audioFile == null || !audioFile.exists()) {
            Toast.makeText(getContext(), "Vui lòng tạo file âm thanh trước", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createAudiobook() {
        progressBar.setVisibility(View.VISIBLE);
        btnSelectAudio.setEnabled(false);
        btnGenerateAudio.setEnabled(false);
        btnCreateAudiobook.setEnabled(false);

        // Thu thập thông tin
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        int publishYear = Integer.parseInt(etPublishYear.getText().toString().trim());
        String description = etDescription.getText().toString().trim();

        // Tạo request body cho file audio
        RequestBody audioRequest = RequestBody.create(MediaType.parse("audio/mpeg"), audioFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("audio", audioFile.getName(), audioRequest);

        // Tạo request body cho các trường khác
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody authorBody = RequestBody.create(MediaType.parse("text/plain"), author);
        RequestBody publishYearBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(publishYear));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody isFreeBody = RequestBody.create(MediaType.parse("text/plain"), "true");
        RequestBody categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), UUID.randomUUID().toString());
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), UUID.randomUUID().toString()); // Thay bằng userId thực tế

        Call<AudiobookResponse> call = apiService.createAudiobook(
                audioPart, titleBody, authorBody, publishYearBody, descriptionBody,
                isFreeBody, categoryIdBody, userIdBody
        );
        call.enqueue(new Callback<AudiobookResponse>() {
            @Override
            public void onResponse(Call<AudiobookResponse> call, Response<AudiobookResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSelectAudio.setEnabled(true);
                btnGenerateAudio.setEnabled(true);
                btnCreateAudiobook.setEnabled(true);

                Log.d(TAG, "Create audiobook response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    AudiobookResponse audiobook = response.body();
                    // Lưu vào cơ sở dữ liệu (giả sử dùng Room hoặc SQLite)
                    saveToDatabase(audiobook);
                    Toast.makeText(getContext(), "Đã tạo Audiobook: " + audiobook.getTitle(), Toast.LENGTH_LONG).show();
                    // Quay lại màn hình trước
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AudiobookResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSelectAudio.setEnabled(true);
                btnGenerateAudio.setEnabled(true);
                btnCreateAudiobook.setEnabled(true);

                Log.e(TAG, "Create audiobook connection error: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getClass().getSimpleName() + " - " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToDatabase(AudiobookResponse audiobook) {
        // Giả sử sử dụng Room hoặc SQLite để lưu
        // Ví dụ:
        // AudiobookEntity entity = new AudiobookEntity(
        //     audiobook.getTitle(),
        //     audiobook.getAuthor(),
        //     audiobook.getPublishedYear(),
        //     audiobook.getDescription(),
        //     audiobook.getCoverImage(),
        //     audiobook.getIsFree(),
        //     audiobook.getDuration(),
        //     audiobook.getFemaleAudioUrl(),
        //     audiobook.getMaleAudioUrl(),
        //     audiobook.getCategoryId(),
        //     audiobook.getUserId()
        // );
        // AppDatabase.getInstance(getContext()).audiobookDao().insert(entity);
        Log.d(TAG, "Saved audiobook to database: " + audiobook.getTitle());
    }

    public interface ApiService {
        @Multipart
        @POST("transcribe/")
        Call<ApiResponse> uploadAudio(@Part MultipartBody.Part file);

        @POST("tts/")
        Call<okhttp3.ResponseBody> textToSpeech(@Body TextToSpeechRequest request);

        @Multipart
        @POST("audiobooks/create/")
        Call<AudiobookResponse> createAudiobook(
                @Part MultipartBody.Part audio,
                @Part("title") RequestBody title,
                @Part("author") RequestBody author,
                @Part("publishedYear") RequestBody publishedYear,
                @Part("description") RequestBody description,
                @Part("isFree") RequestBody isFree,
                @Part("categoryId") RequestBody categoryId,
                @Part("userId") RequestBody userId
        );
    }

    public static class ApiResponse {
        private String text;
        private String source;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class TextToSpeechRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class AudiobookResponse {
        private String title;
        private String author;
        private int publishedYear;
        private String description;
        private String coverImage;
        private boolean isFree;
        private int duration;
        private String femaleAudioUrl;
        private String maleAudioUrl;
        private String categoryId;
        private String userId;

        // Getter và Setter
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getPublishedYear() {
            return publishedYear;
        }

        public void setPublishedYear(int publishedYear) {
            this.publishedYear = publishedYear;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public boolean getIsFree() {
            return isFree;
        }

        public void setIsFree(boolean isFree) {
            this.isFree = isFree;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getFemaleAudioUrl() {
            return femaleAudioUrl;
        }

        public void setFemaleAudioUrl(String femaleAudioUrl) {
            this.femaleAudioUrl = femaleAudioUrl;
        }

        public String getMaleAudioUrl() {
            return maleAudioUrl;
        }

        public void setMaleAudioUrl(String maleAudioUrl) {
            this.maleAudioUrl = maleAudioUrl;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}