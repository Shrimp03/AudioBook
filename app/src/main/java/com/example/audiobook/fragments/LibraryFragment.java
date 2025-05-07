package com.example.audiobook.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.audiobook.R;
import com.example.audiobook.utils.FileUtils;

import java.io.File;
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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";
    private Button btnSelectAudio, btnUpload;
    private TextView tvResult;
    private Uri selectedAudioUri;
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
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        btnSelectAudio = view.findViewById(R.id.btn_select_audio);
        btnUpload = view.findViewById(R.id.btn_upload);
        tvResult = view.findViewById(R.id.tv_result);

        // Cấu hình OkHttp với timeout
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.11.12.174:8000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnSelectAudio.setOnClickListener(v -> openFilePicker());

        btnUpload.setOnClickListener(v -> {
            if (selectedAudioUri != null) {
                uploadAudio();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn file MP3 trước", Toast.LENGTH_SHORT).show();
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
            File file = new File(filePath);

            // Kiểm tra kích thước file
            long maxFileSize = 20 * 1024 * 1024; // 10MB
            Log.d(TAG, "File size: " + file.length() + " bytes");
            if (file.length() > maxFileSize) {
                Toast.makeText(getContext(), "File MP3 quá lớn (giới hạn 10MB)", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Log.d(TAG, "Uploading file: " + file.getName());
            Call<ApiResponse> call = apiService.uploadAudio(body);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d(TAG, "Response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        tvResult.setText(response.body().getText());
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "Connection error: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getClass().getSimpleName() + " - " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi: " + e.getClass().getSimpleName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public interface ApiService {
        @Multipart
        @POST("transcribe/")
        Call<ApiResponse> uploadAudio(@Part MultipartBody.Part file);
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
}