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
import com.example.audiobook.adapters.AudiobookAdapter;
import com.example.audiobook.adapters.CategoryAdapter;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import com.example.audiobook.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView categoryRecyclerView;
    private RecyclerView recommendAudioBookRecycleView;
    private CategoryAdapter categoryAdapter;
    private AudiobookAdapter audiobookAdapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        recommendAudioBookRecycleView = view.findViewById(R.id.recommend_recycler_view);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendAudioBookRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Adapter
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getId());
            bundle.putString("categoryName", category.getName());

            CategoryDetailFragment detailFragment = new CategoryDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        audiobookAdapter = new AudiobookAdapter(new ArrayList<>(), audiobook -> {
            // Handle click nếu cần
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        recommendAudioBookRecycleView.setAdapter(audiobookAdapter);

        // ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        observeViewModel();

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
            audiobookAdapter.updateAudioBooks(audiobooks);
        });

        homeViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
        });
    }
}
