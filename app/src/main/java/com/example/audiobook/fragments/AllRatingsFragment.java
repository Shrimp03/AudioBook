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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.RatingListAdapter;
import com.example.audiobook.viewmodel.AudioBookDetailViewModel;

import java.util.ArrayList;
import java.util.UUID;

public class AllRatingsFragment extends Fragment {

    private RecyclerView ratingsRecyclerView;
    private TextView headerTitle;
    private ImageView backButton;
    private RatingListAdapter ratingListAdapter;
    private AudioBookDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_ratings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        viewModel = new ViewModelProvider(this).get(AudioBookDetailViewModel.class);
        ratingListAdapter = new RatingListAdapter(new ArrayList<>());
        ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ratingsRecyclerView.setAdapter(ratingListAdapter);

        observeViewModel();

        String audioBookId = getArguments() != null ? getArguments().getString("audioBookId") : null;
        String bookTitle = getArguments() != null ? getArguments().getString("bookTitle") : null;
        if (bookTitle != null) {
            setHeaderTitle(bookTitle);
        }
        if (audioBookId != null) {
            try {
                UUID uuid = UUID.fromString(audioBookId);
                viewModel.fetchAllRatings(uuid); // Gọi API lấy tất cả rating
            } catch (IllegalArgumentException e) {
                Log.e("AllRatingsFragment", "Invalid UUID format: " + audioBookId);
            }
        }

        setupClickListeners();
    }

    private void initializeViews(View view) {
        headerTitle = view.findViewById(R.id.header_title);
        backButton = view.findViewById(R.id.back_button);
        ratingsRecyclerView = view.findViewById(R.id.ratings_recycler_view);
    }

    private void observeViewModel() {
        viewModel.ratings.observe(getViewLifecycleOwner(), ratings -> {
            Log.d("AllRatingsFragment", "Loaded ratings: " + ratings.size());
            ratingListAdapter.updateRatings(ratings);
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e("AllRatingsFragment", "Error: " + errorMsg);
            // Có thể hiển thị Toast hoặc Snackbar
        });
    }

    private void setHeaderTitle(String title) {
        if (title != null) {
            if (title.length() > 20) {
                headerTitle.setText(title.substring(0, 17) + "...");
            } else {
                headerTitle.setText(title);
            }
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}