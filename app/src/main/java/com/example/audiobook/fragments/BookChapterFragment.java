package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.adapters.BookChapterAdapter;
import com.example.audiobook.dto.response.BookChapterResponse;
import com.example.audiobook.viewmodel.BookChapterViewModel;

import java.util.ArrayList;

public class BookChapterFragment extends Fragment implements BookChapterAdapter.OnChapterClickListener {

    private static final String TAG = "BookChapterFragment";

    // UI elements
    private TextView headerTitle;

    private RecyclerView bookChapterRecyclerView;
    private BookChapterAdapter bookChapterAdapter;
    private BookChapterViewModel bookChapterViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        headerTitle = view.findViewById(R.id.header_title);

        // Initialize views
        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String audioBookId = arguments.getString("audioBookId");

            // Map data to UI element
            if (title != null) {
                headerTitle.setText(title);
            }

            // Initialize RecyclerView
            bookChapterRecyclerView = view.findViewById(R.id.chapter_list);
            // Set layout
            bookChapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Adapter
            bookChapterAdapter = new BookChapterAdapter(new ArrayList<>(), this);
            // Set adapter
            bookChapterRecyclerView.setAdapter(bookChapterAdapter);

            // View model
            bookChapterViewModel = new ViewModelProvider(this).get(BookChapterViewModel.class);
            // Observe
            observeViewModel();
            // Fetching
            bookChapterViewModel.fetchBookChaptersByAudioBookId(audioBookId);
        }

        // Set click listeners
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        menuButton.setOnClickListener(v -> showToast("Menu clicked"));
    }

    private void observeViewModel() {
        bookChapterViewModel.bookChapters.observe(getViewLifecycleOwner(), bookChapters -> {
            Log.d(TAG, "Loaded book chapters: " + bookChapters.size());
            bookChapterAdapter.updateBookChapters(bookChapters);
        });

        bookChapterViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
        });
    }

    @Override
    public void onChapterClick(BookChapterResponse bookChapterResponse) {
        showToast("Selected: " + bookChapterResponse.getTitle());
        // Here you can navigate to a reading fragment for the selected chapter
        // For example, navigate to ReadBookFragment with the chapter content
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}