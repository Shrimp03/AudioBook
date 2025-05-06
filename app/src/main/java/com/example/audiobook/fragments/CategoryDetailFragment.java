// CategoryDetailFragment.java
package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.adapters.CategoryDetailAdapter;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.viewmodel.CategoryDetailViewModel;

import java.util.ArrayList;

public class CategoryDetailFragment extends Fragment {

    private static final String TAG = "CategoryDetailFragment";
    private String categoryId;
    private String categoryName;

    private ImageView imageViewBack;
    private TextView textViewCategoryTitle;
    private RecyclerView recyclerViewAudiobooks;
    private CategoryDetailAdapter audioBookAdapter;
    private CategoryDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);

        // Get arguments
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            categoryName = getArguments().getString("categoryName");
        }

        // Initialize UI components
        imageViewBack = view.findViewById(R.id.detail_back);
        textViewCategoryTitle = view.findViewById(R.id.detail_category_title);
        recyclerViewAudiobooks = view.findViewById(R.id.recycler_audiobook);

        // Set category title
        textViewCategoryTitle.setText(categoryName);

        // Set back button listeners
        imageViewBack.setOnClickListener(v -> requireActivity().onBackPressed());
        textViewCategoryTitle.setOnClickListener(v -> requireActivity().onBackPressed());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CategoryDetailViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Observe ViewModel data
        observeViewModel();

        // Fetch audiobooks for category
        viewModel.fetchAudiobooksByCategory(categoryId);

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewAudiobooks.setLayoutManager(new GridLayoutManager(getContext(), 2));
        audioBookAdapter = new CategoryDetailAdapter(new ArrayList<>(), this::navigateToAudioBookDetail);
        recyclerViewAudiobooks.setAdapter(audioBookAdapter);
    }

    private void observeViewModel() {
        // Observe audiobooks
        viewModel.audiobooks.observe(getViewLifecycleOwner(), audiobooks -> {
            Log.d(TAG, "Loaded audiobooks: " + audiobooks.size());
            audioBookAdapter.updateAudioBooks(audiobooks);
        });

        // Observe errors
        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
        });
    }

    private void navigateToAudioBookDetail(AudioBookResponse audioBook) {
        Bundle bundle = new Bundle();
        bundle.putString("audioBookId", audioBook.getId().toString());
        bundle.putString("title", audioBook.getTitle());
        bundle.putString("author", audioBook.getAuthor());
        bundle.putString("description", audioBook.getDescription());
        bundle.putString("coverImage", audioBook.getCoverImage());
        bundle.putString("categoryName", audioBook.getCategoryResponse().getName());
        bundle.putString("maleAudioUrl", audioBook.getMaleAudioUrl());

        AudioBookDetailFragment fragment = new AudioBookDetailFragment();
        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}