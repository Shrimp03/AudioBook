package com.example.audiobook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.adapters.LibraryAdapter;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.AudioBookRepository;
import com.example.audiobook.viewmodel.LibraryViewModel;
import com.example.audiobook.viewmodel.LoginViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";
    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvAudioList;
    private ImageView settingImageView;
    private FloatingActionButton fabUploadAudio;
    private ProgressBar progressBar;
    private LinearLayout noResultsLayout;
    private LibraryAdapter libraryAdapter;
    private LibraryViewModel libraryViewModel;
    private AudioBookRepository audioBookRepository;

    private String authToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        SharedPreferences preferences = requireActivity().getSharedPreferences(LoginViewModel.PREFS_NAME, Context.MODE_PRIVATE);
        authToken = preferences.getString(LoginViewModel.TOKEN_KEY, null);
        try {
            view = inflater.inflate(R.layout.fragment_library, container, false);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi tải giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        // Initialize UI components
        try {
            etSearch = view.findViewById(R.id.et_search);
            btnSearch = view.findViewById(R.id.btn_search);
            rvAudioList = view.findViewById(R.id.rv_audio_list);
            settingImageView = view.findViewById(R.id.ic_setting);
            fabUploadAudio = view.findViewById(R.id.fab_upload_audio);
            progressBar = view.findViewById(R.id.progressBar);
            noResultsLayout = view.findViewById(R.id.no_results_layout);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        // Initialize ViewModel and Repository
        try {
            libraryViewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
            audioBookRepository = new AudioBookRepository();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ViewModel or Repository: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo ViewModel/Repository: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Observe ViewModel data
        observeViewModel();

        // Setup search button
        setupSearchButton();

        // Setup FAB
        setupFab();

        // Setup settings button
        setupSettingsButton();

        // Load initial library data
        try {
            progressBar.setVisibility(View.VISIBLE);
            libraryViewModel.fetchUserAudioBooks(authToken); // Tải danh sách audiobook của user
        } catch (Exception e) {
            Log.e(TAG, "Error loading library: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi tải thư viện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    private void setupRecyclerView() {
        try {
            rvAudioList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            libraryAdapter = new LibraryAdapter(
                    new ArrayList<>(),
                    this::navigateToAudioBookDetail, // Callback cho nhấn item
                    this::editAudioBook, // Callback cho "Cập nhật"
                    this::deleteAudioBook // Callback cho "Xóa"
            );
            rvAudioList.setAdapter(libraryAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void observeViewModel() {
        try {
            libraryViewModel.searchResults.observe(getViewLifecycleOwner(), audiobooks -> {
                try {
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar sau khi có kết quả
                    if (audiobooks != null && !audiobooks.isEmpty()) {
                        // Có kết quả: hiển thị RecyclerView, ẩn no_results_layout
                        Log.d(TAG, "Loaded search results: " + audiobooks.size());
                        libraryAdapter.updateAudioBooks(audiobooks);
                        rvAudioList.setVisibility(View.VISIBLE);
                        noResultsLayout.setVisibility(View.GONE);
                    } else {
                        // Không có kết quả: ẩn RecyclerView, hiển thị no_results_layout
                        libraryAdapter.updateAudioBooks(new ArrayList<>());
                        rvAudioList.setVisibility(View.GONE);
                        noResultsLayout.setVisibility(View.VISIBLE);
                        Log.d(TAG, "No search results found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating search results: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi cập nhật kết quả: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

            libraryViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
                Log.e(TAG, "Error from ViewModel: " + errorMsg);
                Toast.makeText(getContext(), "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                rvAudioList.setVisibility(View.GONE);
                noResultsLayout.setVisibility(View.VISIBLE); // Hiển thị khi có lỗi
            });
        } catch (Exception e) {
            Log.e(TAG, "Error observing ViewModel: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi quan sát dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {
            try {
                String query = etSearch.getText().toString().trim();
                if (query.isEmpty()) {
                    // Khi query rỗng, tải lại toàn bộ danh sách audiobook của user
                    Toast.makeText(getContext(), "Hiển thị tất cả sách của bạn", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    libraryViewModel.fetchUserAudioBooks(authToken);
                    return;
                }
                Log.d(TAG, "Searching library for: " + query);
                progressBar.setVisibility(View.VISIBLE);
                libraryViewModel.searchAudiobooksWithUser(authToken, query);
            } catch (Exception e) {
                Log.e(TAG, "Error during search: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setupFab() {
        fabUploadAudio.setOnClickListener(v -> {
            try {
                UploadAudioFragment fragment = new UploadAudioFragment();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to UploadAudioFragment: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSettingsButton() {
        settingImageView.setOnClickListener(v -> {
            try {
                SettingFragment settingFragment = new SettingFragment();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, settingFragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to SettingFragment: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToAudioBookDetail(AudioBookResponse audioBook) {
        try {
            if (audioBook == null) {
                Log.e(TAG, "AudioBookResponse is null in navigateToAudioBookDetail");
                Toast.makeText(getContext(), "Không thể mở chi tiết sách", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putString("audioBookId", audioBook.getId() != null ? audioBook.getId().toString() : "");
            bundle.putString("title", audioBook.getTitle() != null ? audioBook.getTitle() : "");
            bundle.putString("author", audioBook.getAuthor() != null ? audioBook.getAuthor() : "");
            bundle.putString("description", audioBook.getDescription() != null ? audioBook.getDescription() : "");
            bundle.putString("coverImage", audioBook.getCoverImage() != null ? audioBook.getCoverImage() : "");
            bundle.putString("categoryName", audioBook.getCategoryResponse() != null && audioBook.getCategoryResponse().getName() != null ? audioBook.getCategoryResponse().getName() : "");
            bundle.putString("maleAudioUrl", audioBook.getMaleAudioUrl() != null ? audioBook.getMaleAudioUrl() : "");
            bundle.putString("content",audioBook.getTextContent());
            AudioBookDetailFragment fragment = new AudioBookDetailFragment();
            fragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to AudioBookDetailFragment: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi điều hướng chi tiết sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void editAudioBook(AudioBookResponse audioBook) {
        try {
            if (audioBook == null) {
                Log.e(TAG, "AudioBookResponse is null in editAudioBook");
                Toast.makeText(getContext(), "Không thể mở chỉnh sửa sách", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Bundle để truyền dữ liệu audiobook
            Bundle bundle = new Bundle();
            bundle.putString("audioBookId", audioBook.getId() != null ? audioBook.getId().toString() : "");
            bundle.putString("title", audioBook.getTitle() != null ? audioBook.getTitle() : "");
            bundle.putString("author", audioBook.getAuthor() != null ? audioBook.getAuthor() : "");
            bundle.putString("description", audioBook.getDescription() != null ? audioBook.getDescription() : "");
            bundle.putString("coverImage", audioBook.getCoverImage() != null ? audioBook.getCoverImage() : "");
            bundle.putString("categoryName", audioBook.getCategoryResponse() != null && audioBook.getCategoryResponse().getName() != null ? audioBook.getCategoryResponse().getName() : "");
            bundle.putString("maleAudioUrl", audioBook.getMaleAudioUrl() != null ? audioBook.getMaleAudioUrl() : "");
            bundle.putString("femaleAudioUrl", audioBook.getFemaleAudioUrl() != null ? audioBook.getFemaleAudioUrl() : "");
            bundle.putInt("duration",  audioBook.getDuration() );
            bundle.putInt("publishedYear",audioBook.getPublishedYear() );
            bundle.putBoolean("isFree", audioBook.isFree());
            bundle.putString("textContent", audioBook.getTextContent() != null ? audioBook.getTextContent() : "");

            // Điều hướng đến UpdateAudioFragment
            UpdateAudioFragment fragment = new UpdateAudioFragment();
            fragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to UpdateAudioFragment: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi điều hướng chỉnh sửa sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAudioBook(AudioBookResponse audioBook) {
        try {
            if (audioBook == null || audioBook.getId() == null) {
                Log.e(TAG, "AudioBookResponse or ID is null in deleteAudioBook");
                Toast.makeText(getContext(), "Không thể xóa sách", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            Call<ResponseObject> call = audioBookRepository.deleteAudioBook(authToken, audioBook.getId().toString());
            call.enqueue(new Callback<ResponseObject>() {
                @Override
                public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Xóa audiobook thành công", Toast.LENGTH_SHORT).show();
                        // Làm mới danh sách sau khi xóa
                        libraryViewModel.fetchUserAudioBooks(authToken);
                    } else {
                        Log.e(TAG, "Delete audiobook failed: HTTP " + response.code());
                        Toast.makeText(getContext(), "Lỗi server: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObject> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Delete audiobook error: " + t.getMessage(), t);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error deleting audiobook: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi xóa sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            progressBar.setVisibility(View.VISIBLE);
            libraryViewModel.fetchUserAudioBooks(authToken); // Làm mới danh sách audiobook của user
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing library: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi làm mới danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }
}