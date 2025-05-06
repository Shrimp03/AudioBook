// SearchFragment.java
package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private EditText searchEditText;
    private ImageButton searchButton;
    private ImageView settingImageView;
    private RecyclerView searchRecyclerView;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.fragment_search, container, false);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi tải giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        // Initialize UI components
        try {
            searchEditText = view.findViewById(R.id.textSearch);
            searchButton = view.findViewById(R.id.btnSearch);
            settingImageView = view.findViewById(R.id.ic_setting);
            searchRecyclerView = view.findViewById(R.id.category_recycler_view);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        // Setup setting button
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

        return view;
    }

    private void setupRecyclerView() {
        try {
            searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            searchAdapter = new SearchAdapter(new ArrayList<>(), this::navigateToAudioBookDetail);
            searchRecyclerView.setAdapter(searchAdapter);
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
        searchButton.setOnClickListener(v -> {
            try {
                String query = searchEditText.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                    searchAdapter.updateAudioBooks(new ArrayList<>());
                    return;
                }
                Log.d(TAG, "Searching for: " + query);
                searchViewModel.searchAudiobooks(query);
            } catch (Exception e) {
                Log.e(TAG, "Error during search: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
}