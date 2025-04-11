package com.example.audiobook.fragments;

import android.os.Bundle;
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

    // UI elements
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

        // Initialize UI elements
        headerTitle = view.findViewById(R.id.header_title);
        bookCover = view.findViewById(R.id.book_cover);
        bookTitle = view.findViewById(R.id.book_title);
        bookAuthor = view.findViewById(R.id.book_author);
        bookDescription = view.findViewById(R.id.book_description);
        categoryName = view.findViewById(R.id.category_name);

        // Initialize views
        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);
        Button playButton = view.findViewById(R.id.play_button);
        Button readButton = view.findViewById(R.id.read_button);
        TextView seeMore = view.findViewById(R.id.see_more);

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String audiobookId = arguments.getString("audiobookId");
            String title = arguments.getString("title");
            String author = arguments.getString("author");
            String description = arguments.getString("description");
            String coverImage = arguments.getString("coverImage");
            String categoryName = arguments.getString("categoryName");
            String maleAudioUrl = arguments.getString("maleAudioUrl");

            // Map data to UI elements
            if (title != null) {
                bookTitle.setText(title);
                // Truncate the title for the header if it's too long
                if (title.length() > 20) {
                    headerTitle.setText(title.substring(0, 17) + "...");
                } else {
                    headerTitle.setText(title);
                }
            }
            if (author != null) {
                bookAuthor.setText(author);
            }
            if (description != null) {
                bookDescription.setText(description);
            }
            if (categoryName != null) {
                this.categoryName.setText(categoryName);
            }
            // Load the cover image using Glide
            if (coverImage != null && getContext() != null) {
                String imageUrl = APIconst.BASE_URL + "/" +  coverImage;
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_book) // Fallback to placeholder
                        .error(R.drawable.placeholder_book) // Fallback if loading fails
                        .into(bookCover);
            }

            playButton.setOnClickListener(v -> {
                // Navigate to PlayerFragment with audiobook data
                PlayerFragment playerFragment = new PlayerFragment();
                playerFragment.setArguments(arguments); // Pass the same bundle
                if (getActivity() != null) {
                    playerFragment.show(getActivity().getSupportFragmentManager(), "PlayerFragment");
                }
            });
        }

        // Set click listeners
        backButton.setOnClickListener(v -> {
            // Navigate back (pop the fragment from the back stack)
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        menuButton.setOnClickListener(v -> {
            // Show a menu (e.g., using a PopupMenu)
            // For now, we'll just log a message
            // You can implement a PopupMenu here
            android.util.Log.d("AudiobookDetail", "Three-dot menu clicked");
        });


        readButton.setOnClickListener(v -> {
            // Handle read button click (e.g., open the book in a reader)
            android.util.Log.d("AudiobookDetail", "Read button clicked");
        });

        seeMore.setOnClickListener(v -> {
            // Handle see more click (e.g., expand review or navigate to full reviews)
            android.util.Log.d("AudiobookDetail", "See More clicked");
        });
    }
}