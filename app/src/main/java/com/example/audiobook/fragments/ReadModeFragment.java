package com.example.audiobook.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.audiobook.R;

public class ReadModeFragment extends DialogFragment {

    // UI elements
    private TextView headerTitle;
    private TextView bookDescription;
    private TextView progressText;
    private SeekBar progressSlider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the style to fullscreen
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        headerTitle = view.findViewById(R.id.header_title);
        bookDescription = view.findViewById(R.id.book_description);
        progressText = view.findViewById(R.id.progress_text);
        progressSlider = view.findViewById(R.id.progress_slider);

        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String description = arguments.getString("description");

            // Map data to UI elements
            if (title != null) {
                if (title.length() > 20) {
                    headerTitle.setText(title.substring(0, 17) + "...");
                } else {
                    headerTitle.setText(title);
                }
            }
            if (description != null) {
                bookDescription.setText(description);
            }
        }

        // Mock total pages (replace with actual data if available)
        final int totalPages = 278;
        progressSlider.setMax(totalPages);
        progressSlider.setProgress(67); // Starting progress
        updateProgressText(67, totalPages);

        // Set click listeners
        backButton.setOnClickListener(v -> dismiss());

        menuButton.setOnClickListener(v -> showToast("Menu clicked"));

        progressSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressText(progress, totalPages);
                    // Here you can implement logic to scroll the description to the corresponding section
                    // For simplicity, we're just updating the progress text
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Pause any animations or updates
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Resume any animations or updates
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set layout to fullscreen
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    private void updateProgressText(int currentPage, int totalPages) {
        progressText.setText(currentPage + " of " + totalPages);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}