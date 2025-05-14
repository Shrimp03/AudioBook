package com.example.audiobook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.audiobook.R;
import com.example.audiobook.dto.request.AudioBookCreateRequest;
import com.example.audiobook.dto.response.AudioBookCreateResponse;
import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UploadResponse;
import com.example.audiobook.repository.AudioBookRepository;
import com.example.audiobook.repository.CategoryRepository;
import com.example.audiobook.utils.Constant;
import com.example.audiobook.utils.FileUtils;
import com.example.audiobook.viewmodel.LoginViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class UploadAudioFragment extends Fragment {

    private static final String TAG = "UploadAudioFragment";
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private Button btnSelectImage, btnUploadImage, btnSelectFemaleAudio, btnUploadFemaleAudio,
            btnSelectMaleAudio, btnUploadMaleAudio, btnCreateAudiobook;
    private EditText etTitle, etAuthor, etPublishYear, etDescription, etDuration,
            etCoverImage, etFemaleAudioUrl, etMaleAudioUrl, editTxtAudioBook;
    private CheckBox cbIsFree;
    private Spinner spinnerCategory;
    private ProgressBar progressBar;
    private Uri selectedImageUri, selectedFemaleAudioUri, selectedMaleAudioUri;
    private ApiService apiService;
    private AudioBookRepository audioBookRepository;
    private CategoryRepository categoryRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher, femaleAudioPickerLauncher, maleAudioPickerLauncher;
    private String authToken; // Lưu token từ SharedPreferences
    private List<CategoryResponse> categoryList = new ArrayList<>(); // Lưu danh sách category từ API

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy token từ SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences(LoginViewModel.PREFS_NAME, Context.MODE_PRIVATE);
        authToken = preferences.getString(LoginViewModel.TOKEN_KEY, null);

        // Khởi tạo repository
        audioBookRepository = new AudioBookRepository();
        categoryRepository = new CategoryRepository(authToken);

        // Launcher để chọn ảnh bìa
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Toast.makeText(getContext(), "Đã chọn ảnh bìa", Toast.LENGTH_SHORT).show();
                    }
                });

        // Launcher để chọn audio giọng nữ
        femaleAudioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedFemaleAudioUri = result.getData().getData();
                        Toast.makeText(getContext(), "Đã chọn file MP3 giọng nữ", Toast.LENGTH_SHORT).show();
                        // Lấy thời lượng file âm thanh và cập nhật etDuration
                        String duration = getAudioDuration(selectedFemaleAudioUri);
                        if (duration != null) {
                            etDuration.setText(duration);
                        } else {
                            etDuration.setText("");
                            Toast.makeText(getContext(), "Không thể lấy thời lượng file âm thanh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Launcher để chọn audio giọng nam
        maleAudioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedMaleAudioUri = result.getData().getData();
                        Toast.makeText(getContext(), "Đã chọn file MP3 giọng nam", Toast.LENGTH_SHORT).show();
                        // Lấy thời lượng file âm thanh và cập nhật etDuration
                        String duration = getAudioDuration(selectedMaleAudioUri);
                        if (duration != null) {
                            etDuration.setText(duration);
                        } else {
                            etDuration.setText("");
                            Toast.makeText(getContext(), "Không thể lấy thời lượng file âm thanh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_audio, container, false);

        // Ánh xạ các thành phần giao diện
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnUploadImage = view.findViewById(R.id.btn_upload_image);
        btnSelectFemaleAudio = view.findViewById(R.id.btn_select_female_audio);
        btnUploadFemaleAudio = view.findViewById(R.id.btn_upload_female_audio);
        btnSelectMaleAudio = view.findViewById(R.id.btn_select_male_audio);
        btnUploadMaleAudio = view.findViewById(R.id.btn_upload_male_audio);
        btnCreateAudiobook = view.findViewById(R.id.btn_create_audiobook);
        etTitle = view.findViewById(R.id.et_title);
        etAuthor = view.findViewById(R.id.et_author);
        etPublishYear = view.findViewById(R.id.et_publish_year);
        etDescription = view.findViewById(R.id.et_description);
        etDuration = view.findViewById(R.id.et_duration);
        etCoverImage = view.findViewById(R.id.et_cover_image);
        etFemaleAudioUrl = view.findViewById(R.id.et_female_audio_url);
        etMaleAudioUrl = view.findViewById(R.id.et_male_audio_url);
        editTxtAudioBook = view.findViewById(R.id.editTxt_audioBook);
        cbIsFree = view.findViewById(R.id.cb_is_free);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        progressBar = view.findViewById(R.id.progress_bar_upload);

        // Cấu hình OkHttp với timeout
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Khởi tạo Retrofit cho API transcribe
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_AI)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Cấu hình Spinner danh mục
        setupCategorySpinner();

        // Gắn sự kiện click
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnUploadImage.setOnClickListener(v -> uploadImage());
        btnSelectFemaleAudio.setOnClickListener(v -> openFemaleAudioPicker());
        btnUploadFemaleAudio.setOnClickListener(v -> uploadFemaleAudio());
        btnSelectMaleAudio.setOnClickListener(v -> openMaleAudioPicker());
        btnUploadMaleAudio.setOnClickListener(v -> uploadMaleAudio());
        btnCreateAudiobook.setOnClickListener(v -> createAudiobook());

        return view;
    }

    private void setupCategorySpinner() {
        // Danh sách mặc định (fallback) nếu API thất bại
        List<String> defaultCategories = Arrays.asList("Fiction", "Non-Fiction", "Science", "History", "Fantasy", "Biography");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, defaultCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Kiểm tra token trước khi gọi API
        if (authToken == null) {
            Toast.makeText(getContext(), "Không có token, không thể tải danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API lấy danh sách category từ CategoryRepository
        Call<List<CategoryResponse>> call = categoryRepository.getAllCategories();
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }
                    ArrayAdapter<String> apiAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, categoryNames);
                    apiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(apiAdapter);
                } else {
                    Toast.makeText(getContext(), "Lỗi tải danh mục: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Get categories error: " + t.getMessage());
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openFemaleAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        femaleAudioPickerLauncher.launch(intent);
    }

    private void openMaleAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        maleAudioPickerLauncher.launch(intent);
    }

    private String getAudioDuration(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getContext(), uri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();

            if (durationStr != null) {
                long durationMs = Long.parseLong(durationStr);
                // Chuyển đổi mili giây sang định dạng mm:ss
                long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
                return String.format("%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting audio duration: " + e.getMessage());
        }
        return null;
    }

    private Integer parseDurationToSeconds(String duration) {
        try {
            String[] parts = duration.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes * 60 + seconds;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing duration: " + e.getMessage());
            return null;
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn ảnh bìa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authToken == null) {
            Toast.makeText(getContext(), "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String filePath = FileUtils.getPath(getContext(), selectedImageUri);
            if (filePath == null) {
                Toast.makeText(getContext(), "Không thể lấy đường dẫn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(getContext(), "File ảnh không tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (file.length() > MAX_FILE_SIZE) {
                Toast.makeText(getContext(), "File ảnh quá lớn (giới hạn 20MB)", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Log.d(TAG, "Uploading image: " + file.getName());
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            Call<UploadResponse> call = audioBookRepository.uploadImage(body);
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.d(TAG, "Image upload response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse uploadResponse = response.body();
                        etCoverImage.setText(uploadResponse.getFilePath());
                        Toast.makeText(getContext(), "Đã upload ảnh bìa: " + uploadResponse.getFilePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.e(TAG, "Image upload error: " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            enableButtons();

            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFemaleAudio() {
        if (selectedFemaleAudioUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn file audio giọng nữ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authToken == null) {
            Toast.makeText(getContext(), "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String filePath = FileUtils.getPath(getContext(), selectedFemaleAudioUri);
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

            Log.d(TAG, "Uploading female audio: " + file.getName());
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            // Kiểm tra xem có cần gọi API transcribe không
            boolean shouldTranscribe = editTxtAudioBook.getText().toString().trim().isEmpty() ||
                    etMaleAudioUrl.getText().toString().trim().isEmpty();

            if (shouldTranscribe) {
                // Gọi API transcribe
                Call<ApiResponse> transcribeCall = apiService.uploadAudio(body);
                transcribeCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.d(TAG, "Transcribe response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            editTxtAudioBook.setText(response.body().getText());
                            Toast.makeText(getContext(), "Đã nhận văn bản từ file MP3 giọng nữ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi server transcribe: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Transcribe error: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi kết nối transcribe: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.d(TAG, "Skipping transcribe: editTxtAudioBook and male audio URL already set");
            }

            // Gọi API upload audio
            Call<UploadResponse> uploadCall = audioBookRepository.uploadAudio(body);
            uploadCall.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.d(TAG, "Female audio upload response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse uploadResponse = response.body();
                        etFemaleAudioUrl.setText(uploadResponse.getFilePath());
                        Toast.makeText(getContext(), "Đã upload audio giọng nữ: " + uploadResponse.getFilePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.e(TAG, "Female audio upload error: " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            enableButtons();

            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadMaleAudio() {
        if (selectedMaleAudioUri == null) {
            Toast.makeText(getContext(), "Vui lòng chọn file audio giọng nam", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authToken == null) {
            Toast.makeText(getContext(), "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String filePath = FileUtils.getPath(getContext(), selectedMaleAudioUri);
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

            Log.d(TAG, "Uploading male audio: " + file.getName());
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            // Kiểm tra xem có cần gọi API transcribe không
            boolean shouldTranscribe = editTxtAudioBook.getText().toString().trim().isEmpty() ||
                    etFemaleAudioUrl.getText().toString().trim().isEmpty();

            if (shouldTranscribe) {
                // Gọi API transcribe
                Call<ApiResponse> transcribeCall = apiService.uploadAudio(body);
                transcribeCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        Log.d(TAG, "Transcribe response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            editTxtAudioBook.setText(response.body().getText());
                            Toast.makeText(getContext(), "Đã nhận văn bản từ file MP3 giọng nam", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi server transcribe: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Transcribe error: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi kết nối transcribe: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.d(TAG, "Skipping transcribe: editTxtAudioBook and female audio URL already set");
            }

            // Gọi API upload audio
            Call<UploadResponse> uploadCall = audioBookRepository.uploadAudio(body);
            uploadCall.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.d(TAG, "Male audio upload response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse uploadResponse = response.body();
                        etMaleAudioUrl.setText(uploadResponse.getFilePath());
                        Toast.makeText(getContext(), "Đã upload audio giọng nam: " + uploadResponse.getFilePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.e(TAG, "Male audio upload error: " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            enableButtons();

            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createAudiobook() {
        if (!validateInputs()) {
            return;
        }

        if (authToken == null) {
            Toast.makeText(getContext(), "Không có token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(getContext(), "Danh sách danh mục rỗng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            AudioBookCreateRequest request = new AudioBookCreateRequest();
            request.setTitle(etTitle.getText().toString().trim());
            request.setAuthor(etAuthor.getText().toString().trim());
            request.setPublishedYear(Integer.parseInt(etPublishYear.getText().toString().trim()));
            request.setDescription(etDescription.getText().toString().trim());
            request.setCoverImage(etCoverImage.getText().toString().trim());
            request.setIsFree(cbIsFree.isChecked());

            // Chuyển đổi duration từ mm:ss sang giây
            Integer durationSeconds = parseDurationToSeconds(etDuration.getText().toString().trim());
            if (durationSeconds == null) {
                etDuration.setError("Thời lượng không hợp lệ");
                return;
            }
            request.setDuration(durationSeconds);

            request.setFemaleAudioUrl(etFemaleAudioUrl.getText().toString().trim());
            request.setMaleAudioUrl(etMaleAudioUrl.getText().toString().trim());
            request.setTextContent(editTxtAudioBook.getText().toString().trim());

            // Lấy categoryId từ spinner
            int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();
            if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categoryList.size()) {
                Toast.makeText(getContext(), "Vui lòng chọn danh mục hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            UUID categoryId = UUID.fromString(categoryList.get(selectedCategoryPosition).getId());
            request.setCategoryId(categoryId);

            Log.d(TAG, "Creating audiobook: " + request.getTitle());
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            Call<ResponseObject<AudioBookCreateResponse>> call = audioBookRepository.createAudioBook(authToken, request);
            call.enqueue(new Callback<ResponseObject<AudioBookCreateResponse>>() {
                @Override
                public void onResponse(Call<ResponseObject<AudioBookCreateResponse>> call, Response<ResponseObject<AudioBookCreateResponse>> response) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.d(TAG, "Create audiobook response code: " + response.code());
                    if (!isAdded() || getContext() == null) {
                        Log.e(TAG, "Fragment is detached, cannot show Toast or navigate");
                        return;
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        ResponseObject<AudioBookCreateResponse> responseObject = response.body();
                        if (responseObject.getData() != null) {
                            AudioBookCreateResponse createResponse = responseObject.getData();
                            String message = createResponse.getMessage() != null ? createResponse.getMessage() : "Tạo audiobook thành công";
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Navigating to LibraryFragment");
                            try {
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new LibraryFragment())
                                        .commit();
                            } catch (IllegalStateException e) {
                                Log.e(TAG, "Error navigating to LibraryFragment: " + e.getMessage(), e);
                                Toast.makeText(getContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String errorMessage = responseObject.getMessage() != null ? responseObject.getMessage() : "Không có dữ liệu trả về";
                            Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else if (response.code() == 401) {
                        Toast.makeText(getContext(), "Token hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                    } else {
                        String errorBody = "Unknown error";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading errorBody: " + e.getMessage(), e);
                        }
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject<AudioBookCreateResponse>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    enableButtons();

                    Log.e(TAG, "Create audiobook error: " + t.getMessage(), t);
                    if (isAdded() && getContext() != null) {
                        String errorMessage = t.getMessage() != null ? t.getMessage() : "Lỗi kết nối không xác định";
                        Toast.makeText(getContext(), "Lỗi kết nối: " + errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Fragment is detached, cannot show Toast");
                    }
                }
            });
        } catch (NumberFormatException e) {
            progressBar.setVisibility(View.GONE);
            enableButtons();
            etPublishYear.setError("Năm xuất bản phải là số");
            Log.e(TAG, "Error parsing publish year: " + e.getMessage(), e);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            enableButtons();
            Log.e(TAG, "Error: " + e.getMessage(), e);
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void resetForm() {
        etTitle.setText("");
        etAuthor.setText("");
        etPublishYear.setText("");
        etDescription.setText("");
        etDuration.setText("");
        etCoverImage.setText("");
        etFemaleAudioUrl.setText("");
        etMaleAudioUrl.setText("");
        editTxtAudioBook.setText("");
        cbIsFree.setChecked(false);
        spinnerCategory.setSelection(0);
        selectedImageUri = null;
        selectedFemaleAudioUri = null;
        selectedMaleAudioUri = null;
    }

    private boolean validateInputs() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String publishYear = etPublishYear.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String coverImage = etCoverImage.getText().toString().trim();

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
        try {
            int year = Integer.parseInt(publishYear);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR); // Lấy năm hiện tại động
            if (year < 1800 || year > currentYear) {
                etPublishYear.setError("Năm xuất bản phải từ 1800 đến " + currentYear);
                return false;
            }
        } catch (NumberFormatException e) {
            etPublishYear.setError("Năm xuất bản phải là số hợp lệ");
            return false;
        }
        if (description.isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả");
            return false;
        }
        if (duration.isEmpty()) {
            etDuration.setError("Vui lòng nhập thời lượng");
            return false;
        }
        if (coverImage.isEmpty()) {
            etCoverImage.setError("Vui lòng upload ảnh bìa");
            return false;
        }
        if (etFemaleAudioUrl.getText().toString().trim().isEmpty() && etMaleAudioUrl.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng upload ít nhất một file audio (giọng nam hoặc nữ)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void disableButtons() {
        btnSelectImage.setEnabled(false);
        btnUploadImage.setEnabled(false);
        btnSelectFemaleAudio.setEnabled(false);
        btnUploadFemaleAudio.setEnabled(false);
        btnSelectMaleAudio.setEnabled(false);
        btnUploadMaleAudio.setEnabled(false);
        btnCreateAudiobook.setEnabled(false);
    }

    private void enableButtons() {
        btnSelectImage.setEnabled(true);
        btnUploadImage.setEnabled(true);
        btnSelectFemaleAudio.setEnabled(true);
        btnUploadFemaleAudio.setEnabled(true);
        btnSelectMaleAudio.setEnabled(true);
        btnUploadMaleAudio.setEnabled(true);
        btnCreateAudiobook.setEnabled(true);
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