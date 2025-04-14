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

// Fragment for displaying a list of book chapters
public class BookChapterFragment extends Fragment implements BookChapterAdapter.OnChapterClickListener {

    // Log tag for debugging
    private static final String TAG = "BookChapterFragment";

    // UI components
    private TextView headerTitle;
    private RecyclerView bookChapterRecyclerView;

    // Adapter and ViewModel
    private BookChapterAdapter bookChapterAdapter;
    private BookChapterViewModel bookChapterViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Set up RecyclerView
        setupRecyclerView(view);

        // Initialize ViewModel
        bookChapterViewModel = new ViewModelProvider(this).get(BookChapterViewModel.class);

        // Observe ViewModel data
        observeViewModel();

        // Load data from arguments
        loadDataFromArguments();

        // Set up click listeners
        setupClickListeners(view);
    }

    // Initializes UI components
    private void initializeViews(View view) {
        headerTitle = view.findViewById(R.id.header_title);
    }

    // Sets up RecyclerView with adapter and layout manager
    private void setupRecyclerView(View view) {
        bookChapterRecyclerView = view.findViewById(R.id.chapter_list);
        bookChapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookChapterAdapter = new BookChapterAdapter(new ArrayList<>(), this);
        bookChapterRecyclerView.setAdapter(bookChapterAdapter);
    }

    // Loads data from fragment arguments and fetches chapters
    private void loadDataFromArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String audioBookId = arguments.getString("audioBookId");

            if (title != null) {
                headerTitle.setText(title);
            }
            if (audioBookId != null) {
                bookChapterViewModel.fetchBookChaptersByAudioBookId(audioBookId);
            }
        }
    }

    // Observes ViewModel LiveData for updates
    private void observeViewModel() {
        bookChapterViewModel.bookChapters.observe(getViewLifecycleOwner(), bookChapters -> {
            Log.d(TAG, "Loaded book chapters: " + bookChapters.size());
            bookChapterAdapter.updateBookChapters(bookChapters);
        });

        bookChapterViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e(TAG, "Error: " + errorMsg);
            showToast("Error: " + errorMsg);
        });
    }

    // Sets up click listeners for back and menu buttons
    private void setupClickListeners(View view) {
        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        menuButton.setOnClickListener(v -> showToast("Menu clicked"));
    }

    // Handles chapter click events
    @Override
    public void onChapterClick(BookChapterResponse bookChapterResponse) {
        showToast("Selected: " + bookChapterResponse.getTitle());

        // Navigate to ReadModeFragment with chapter content
        Bundle bundle = new Bundle();
        bundle.putString("title", bookChapterResponse.getTitle());
        bundle.putString("description", bookChapterResponse.getTextContent()); // Assuming content is the chapter text

        ReadModeFragment readModeFragment = new ReadModeFragment();
        readModeFragment.setArguments(bundle);

        if (getActivity() != null) {
            readModeFragment.show(getActivity().getSupportFragmentManager(), "ReadModeFragment");
        }
    }

    // Displays a toast message
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}