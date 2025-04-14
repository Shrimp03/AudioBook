// HomeFragment.java
package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.adapters.HomeAudioBookAdapter;
import com.example.audiobook.adapters.CategoryAdapter;
import com.example.audiobook.viewmodel.HomeViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView categoryRecyclerView;
    private RecyclerView recommendAudioBookRecycleView;
    private CategoryAdapter categoryAdapter;
    private HomeAudioBookAdapter recommendAudioBookAdapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        recommendAudioBookRecycleView = view.findViewById(R.id.recommend_recycler_view);
        // Set layout
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendAudioBookRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // Adapter
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getId());
            bundle.putString("categoryName", category.getName());

            CategoryDetailFragment categoryDetailFragment = new CategoryDetailFragment();
            categoryDetailFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, categoryDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recommendAudioBookAdapter = new HomeAudioBookAdapter(new ArrayList<>(), audiobook -> {
            Bundle bundle = new Bundle();
            bundle.putString("audioBookId", audiobook.getId().toString()); // Convert UUID to String
            bundle.putString("title", audiobook.getTitle()); // Title
            bundle.putString("author", audiobook.getAuthor()); // Author
            bundle.putString("description", audiobook.getDescription()); // Description
            bundle.putString("coverImage", audiobook.getCoverImage()); // Cover image URL
            bundle.putString("categoryName", audiobook.getCategoryResponse().getName()); // CategoryName
            bundle.putString("maleAudioUrl", audiobook.getMaleAudioUrl()); // Male Audio Url

            AudioBookDetailFragment audioBookDetailFragment = new AudioBookDetailFragment();
            audioBookDetailFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, audioBookDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Set adapter
        categoryRecyclerView.setAdapter(categoryAdapter);
        recommendAudioBookRecycleView.setAdapter(recommendAudioBookAdapter);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // Observe
        observeViewModel();
        // Fetching
        homeViewModel.fetchCategories();
        homeViewModel.fetchRecommendedAudiobooks();

        return view;
    }

    private void observeViewModel() {
        homeViewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            Log.d(TAG, "Loaded categories: " + categories.size());
            categoryAdapter.updateCategories(categories);
        });

        homeViewModel.recommendedAudiobooks.observe(getViewLifecycleOwner(), audiobooks -> {
            Log.d(TAG, "Loaded audiobooks: " + audiobooks.size());
            recommendAudioBookAdapter.updateAudioBooks(audiobooks);
        });

        homeViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
        });
    }
}
