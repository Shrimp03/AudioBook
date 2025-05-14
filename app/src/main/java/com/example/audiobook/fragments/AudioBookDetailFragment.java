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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.adapters.RatingPagerAdapter;
import com.example.audiobook.api.APIconst;
import com.example.audiobook.viewmodel.AudioBookDetailViewModel;

import java.util.ArrayList;
import java.util.UUID;

public class AudioBookDetailFragment extends Fragment {

    // UI components
    private TextView headerTitle;
    private ImageView bookCover;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookDescription;
    private TextView categoryName;
    private ViewPager2 ratingsViewPager;

    // ViewModel
    private AudioBookDetailViewModel viewModel;

    // Adapter
    private RatingPagerAdapter ratingPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Populate UI with data from arguments
        populateDataFromArguments();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AudioBookDetailViewModel.class);

        // Initialize adapter with empty list
        ratingPagerAdapter = new RatingPagerAdapter(new ArrayList<>());
        ratingsViewPager.setAdapter(ratingPagerAdapter);
        ratingsViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Observe ViewModel data
        observeViewModel();

        // Fetch reviews
        String audioBookId = getArguments() != null ? getArguments().getString("audioBookId") : null;
        if (audioBookId != null) {
            viewModel.fetchRatings(UUID.fromString(audioBookId));
        }

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
        ratingsViewPager = view.findViewById(R.id.reviews_view_pager);
    }

    // Observes ViewModel LiveData for updates
    private void observeViewModel() {
        viewModel.ratings.observe(getViewLifecycleOwner(), ratingResponses -> {
            Log.d("AudioBookDetail", "Loaded reviews: " + ratingResponses.size());
            ratingPagerAdapter.updateReviews(ratingResponses);
        });

        viewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            Log.e("AudioBookDetail", "Error: " + errorMsg);
            // Có thể hiển thị Toast hoặc Snackbar để thông báo lỗi
        });
    }

    // Populates UI with data from the fragment's arguments
    private void populateDataFromArguments() {
        Bundle arguments = getArguments();
        if (arguments == null) return;

        String title = arguments.getString("title");
        String author = arguments.getString("author");
        String description = arguments.getString("description");
        String coverImage = arguments.getString("coverImage");
        String category = arguments.getString("categoryName");

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
        Button addRatingButton = view.findViewById(R.id.add_rating_button);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        menuButton.setOnClickListener(v -> Log.d("AudiobookDetail", "Three-dot menu clicked"));

        playButton.setOnClickListener(v -> {
            PlayerFragment playerFragment = new PlayerFragment();
            playerFragment.setArguments(getArguments());
            if (getActivity() != null) {
                playerFragment.show(getActivity().getSupportFragmentManager(), "PlayerFragment");
            }
        });

        readButton.setOnClickListener(v -> {
            ReadModeFragment readModeFragment = new ReadModeFragment();
            readModeFragment.setArguments(getArguments());
            if (getActivity() != null) {
                readModeFragment.show(getActivity().getSupportFragmentManager(), "ReadModeFragment");
            }
        });

        seeMore.setOnClickListener(v -> {
            AllRatingsFragment allRatingsFragment = new AllRatingsFragment();
            Bundle args = new Bundle();
            args.putString("audioBookId", getArguments() != null ? getArguments().getString("audioBookId") : null);
            args.putString("bookTitle", getArguments() != null ? getArguments().getString("title") : null);
            allRatingsFragment.setArguments(args);

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, allRatingsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        addRatingButton.setOnClickListener(v -> {
            AddRatingFragment addRatingFragment = new AddRatingFragment();
            Bundle args = new Bundle();
            args.putString("audioBookId", getArguments() != null ? getArguments().getString("audioBookId") : null);
            addRatingFragment.setArguments(args);
            addRatingFragment.show(getParentFragmentManager(), "AddRatingFragment");
        });
    }
}