package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.CategoryAdapter;
import com.example.audiobook.adapters.HomeAudioBookAdapter;
import com.example.audiobook.dto.response.AudioBookResponse;
import com.example.audiobook.viewmodel.HomeViewModel;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // Log tag for debugging
    private static final String TAG = "HomeFragment";
    private ImageView settingImageView;

    // UI components
    private RecyclerView categoryRecyclerView;
    private RecyclerView recommendAudioBookRecyclerView;

    // Adapters for RecyclerViews
    private CategoryAdapter categoryAdapter;
    private HomeAudioBookAdapter recommendAudioBookAdapter;

    // ViewModel for data handling
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        settingImageView = view.findViewById(R.id.ic_setting);

        // Initialize RecyclerViews
        setupRecyclerViews(view);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observe ViewModel data
        observeViewModel();

        // Fetch data
        homeViewModel.fetchCategories();
        homeViewModel.fetchRecommendedAudiobooks();

        return view;
    }

    // Sets up RecyclerViews with adapters and layout managers
    private void setupRecyclerViews(View view) {
        // Initialize category RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> navigateToCategoryDetail(category.getId(), category.getName()));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Initialize recommended audiobooks RecyclerView
        recommendAudioBookRecyclerView = view.findViewById(R.id.recommend_recycler_view);
        recommendAudioBookRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendAudioBookAdapter = new HomeAudioBookAdapter(new ArrayList<>(), audiobook -> navigateToAudioBookDetail(audiobook));
        recommendAudioBookRecyclerView.setAdapter(recommendAudioBookAdapter);
    }

    // Observes ViewModel LiveData for updates
    private void observeViewModel() {
        // Observe categories
        homeViewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            Log.d(TAG, "Loaded categories: " + categories.size());
            categoryAdapter.updateCategories(categories);
        });

        // Observe recommended audiobooks
        homeViewModel.recommendedAudiobooks.observe(getViewLifecycleOwner(), audiobooks -> {
            Log.d(TAG, "Loaded audiobooks: " + audiobooks.size());
            recommendAudioBookAdapter.updateAudioBooks(audiobooks);
        });

        // Observe errors
        homeViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
        });
    }

    // Navigates to CategoryDetailFragment with category data
    private void navigateToCategoryDetail(String categoryId, String categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryId", categoryId);
        bundle.putString("categoryName", categoryName);

        CategoryDetailFragment fragment = new CategoryDetailFragment();
        fragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Navigates to AudioBookDetailFragment with audiobook data
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