package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.api.APIconst;

public class AudioBookDetailFragment extends Fragment {

    // UI components
    private TextView headerTitle;
    private ImageView bookCover;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookDescription;
    private TextView categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Populate UI with data from arguments
        populateDataFromArguments();

        // Set up click listeners
        setupClickListeners(view);
    }

    // Initializes UI components
    private void initializeViews(View view) {
        headerTitle = view.findViewById(R.id.header_title);
        bookCover = view.findViewById(R.id.book_cover);
        bookTitle = view.findViewById(R.id.book_title);
        bookAuthor = view.findViewById(R.id.book_author);
        bookDescription = view.findViewById(R.id.book_description);
        categoryName = view.findViewById(R.id.category_name);
    }

    // Populates UI with data from the fragment's arguments
    private void populateDataFromArguments() {
        Bundle arguments = getArguments();
        if (arguments == null) return;

        // Extract data
        String title = arguments.getString("title");
        String author = arguments.getString("author");
        String description = arguments.getString("description");
        String coverImage = arguments.getString("coverImage");
        String category = arguments.getString("categoryName");

        // Set data to views
        setText(bookTitle, title);
        setHeaderTitle(title);
        setText(bookAuthor, author);
        setText(bookDescription, description);
        setText(categoryName, category);
        loadCoverImage(coverImage);
    }

    // Sets text to a TextView if the value is not null
    private void setText(TextView textView, String value) {
        if (value != null) {
            textView.setText(value);
        }
    }

    // Sets the header title, truncating if necessary
    private void setHeaderTitle(String title) {
        if (title != null) {
            if (title.length() > 20) {
                headerTitle.setText(title.substring(0, 17) + "...");
            } else {
                headerTitle.setText(title);
            }
        }
    }

    // Loads the cover image using Glide
    private void loadCoverImage(String coverImage) {
        if (coverImage != null && getContext() != null) {
            String imageUrl = APIconst.BASE_URL + "/" + coverImage;
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(bookCover);
        }
    }

    // Sets up click listeners for interactive elements
    private void setupClickListeners(View view) {
        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);
        Button playButton = view.findViewById(R.id.play_button);
        Button readButton = view.findViewById(R.id.read_button);
        TextView seeMore = view.findViewById(R.id.see_more);

        // Back button click listener
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Menu button click listener
        menuButton.setOnClickListener(v -> Log.d("AudiobookDetail", "Three-dot menu clicked"));

        // Play button click listener
        playButton.setOnClickListener(v -> {
            PlayerFragment playerFragment = new PlayerFragment();
            playerFragment.setArguments(getArguments());
            if (getActivity() != null) {
                playerFragment.show(getActivity().getSupportFragmentManager(), "PlayerFragment");
            }
        });

        // Read button click listener
        readButton.setOnClickListener(v -> {
            BookChapterFragment bookChapterFragment = new BookChapterFragment();
            bookChapterFragment.setArguments(getArguments());
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, bookChapterFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // See more click listener
        seeMore.setOnClickListener(v -> Log.d("AudiobookDetail", "See More clicked"));
    }
}