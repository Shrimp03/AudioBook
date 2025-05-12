package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.adapters.SearchAdapter;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.viewmodel.SearchViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";
    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView rvAudioList;
    private ImageView settingImageView;
    private FloatingActionButton fabUploadAudio;
    private ProgressBar progressBar;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
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
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        // Initialize ViewModel
        try {
            searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ViewModel: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo ViewModel: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
//            progressBar.setVisibility(View.VISIBLE);
            searchViewModel.searchAudiobooks(""); // Placeholder until user-specific method is added
        } catch (Exception e) {
            Log.e(TAG, "Error loading library: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi tải thư viện: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void setupRecyclerView() {
        try {
            rvAudioList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            searchAdapter = new SearchAdapter(new ArrayList<>(), this::navigateToAudioBookDetail);
            rvAudioList.setAdapter(searchAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void observeViewModel() {
        try {
            searchViewModel.searchResults.observe(getViewLifecycleOwner(), audiobooks -> {
                try {
                    if (audiobooks != null) {
                        Log.d(TAG, "Loaded search results: " + audiobooks.size());
                        searchAdapter.updateAudioBooks(audiobooks);
                    } else {
                        searchAdapter.updateAudioBooks(new ArrayList<>());
                        Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating search results: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Lỗi cập nhật kết quả: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            searchViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
                Log.e(TAG, "Error from ViewModel: " + errorMsg);
                Toast.makeText(getContext(), "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error observing ViewModel: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi quan sát dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {
            try {
                String query = etSearch.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                    searchAdapter.updateAudioBooks(new ArrayList<>());
                    return;
                }
                Log.d(TAG, "Searching library for: " + query);
//                progressBar.setVisibility(View.VISIBLE);
                searchViewModel.searchAudiobooks(query);
            } catch (Exception e) {
                Log.e(TAG, "Error during search: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume() {
        super.onResume();
        try {
//            progressBar.setVisibility(View.VISIBLE);
            searchViewModel.searchAudiobooks(""); // Refresh library
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing library: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi làm mới danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
