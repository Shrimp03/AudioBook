package com.example.audiobook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.audiobook.viewmodel.HomeViewModel;
import com.example.audiobook.viewmodel.LoginViewModel;

import java.util.ArrayList;

public class AudioBookListFragment extends Fragment {
    private static final String TAG = "AudioBookListFragment";
    private String listId;
    private String listName;

    private ImageView imageViewBack;
    private TextView textViewListTitle;
    private RecyclerView recyclerViewAudiobooks;
    private CategoryDetailAdapter audioBookAdapter;
    private HomeViewModel viewModel;
    private String authToken; // Lưu token từ SharedPreferences


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audiobook_list, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences(LoginViewModel.PREFS_NAME, Context.MODE_PRIVATE);
        authToken = preferences.getString(LoginViewModel.TOKEN_KEY, null);
        // Get arguments
        if (getArguments() != null) {
            listId = getArguments().getString("listId");
            listName = getArguments().getString("listName");
        }

        // Initialize UI components
        imageViewBack = view.findViewById(R.id.list_back);
        textViewListTitle = view.findViewById(R.id.detail_list_title);
        recyclerViewAudiobooks = view.findViewById(R.id.recycler_audiobook);

        // Set category title
        textViewListTitle.setText(listName);

        // Set back button listeners
        imageViewBack.setOnClickListener(v -> requireActivity().onBackPressed());
        textViewListTitle.setOnClickListener(v -> requireActivity().onBackPressed());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Observe ViewModel data
        observeViewModel();

        // Fetch audiobooks for category
        if(listId == "recommend"){
            viewModel.fetchRecommendedAudiobooks(authToken);
        }
        else {
            viewModel.fetchNewReleaseAudiobooks();
        }

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewAudiobooks.setLayoutManager(new GridLayoutManager(getContext(), 2));
        audioBookAdapter = new CategoryDetailAdapter(new ArrayList<>(), this::navigateToAudioBookDetail);
        recyclerViewAudiobooks.setAdapter(audioBookAdapter);
    }

    private void observeViewModel() {
        // Observe audiobooks
        if(listId == "recommend"){
            viewModel.recommendedAudiobooks.observe(getViewLifecycleOwner(), audiobooks -> {
                Log.d(TAG, "Loaded audiobooks: " + audiobooks.size());
                audioBookAdapter.updateAudioBooks(audiobooks);
            });
        }
        else {
            viewModel.newReleaseAudiobooks.observe(getViewLifecycleOwner(), audiobooks -> {
                Log.d(TAG, "Loaded audiobooks: " + audiobooks.size());
                audioBookAdapter.updateAudioBooks(audiobooks);
            });
        }

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
