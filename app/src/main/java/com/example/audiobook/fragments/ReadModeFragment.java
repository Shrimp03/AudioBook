package com.example.audiobook.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
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
    private ScrollView scrollView;
    private int totalLines;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        scrollView = view.findViewById(R.id.scroll_view);

        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String content = arguments.getString("content");

            // Map data to UI elements
            if (title != null) {
                if (title.length() > 20) {
                    headerTitle.setText(title.substring(0, 17) + "...");
                } else {
                    headerTitle.setText(title);
                }
            }
            if (content != null) {
                bookDescription.setText(content);
            }
        }

        // Set click listeners
        backButton.setOnClickListener(v -> dismiss());
        menuButton.setOnClickListener(v -> showToast("Menu clicked"));

        // Get total lines and set SeekBar max after layout is drawn
        bookDescription.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bookDescription.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                totalLines = bookDescription.getLineCount();
                progressSlider.setMax(totalLines);

                // Initialize progress based on last visible line
                int scrollViewHeight = scrollView.getHeight();
                int lineHeight = bookDescription.getLineHeight();
                int scrollY = scrollView.getScrollY();
                int firstVisibleLine = scrollY / lineHeight;
                int visibleLineCount = scrollViewHeight / lineHeight;
                int lastVisibleLine = Math.min(firstVisibleLine + visibleLineCount, totalLines);
                progressSlider.setProgress(lastVisibleLine);
                updateProgressText(lastVisibleLine, totalLines);
            }
        });

        // SeekBar listener to scroll content
        progressSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressText(progress, totalLines);
                    // Scroll to the corresponding line
                    if (totalLines > 0) {
                        int lineHeight = bookDescription.getLineHeight();
                        int scrollY = (progress - 1) * lineHeight;
                        scrollView.smoothScrollTo(0, scrollY);
                    }
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

        // ScrollView listener to update SeekBar based on last visible line
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (totalLines > 0) {
                // Calculate the last visible line
                int scrollViewHeight = scrollView.getHeight();
                int lineHeight = bookDescription.getLineHeight();
                int firstVisibleLine = scrollY / lineHeight;
                int visibleLineCount = scrollViewHeight / lineHeight;
                int lastVisibleLine = Math.min(firstVisibleLine + visibleLineCount, totalLines);
                progressSlider.setProgress(lastVisibleLine);
                updateProgressText(lastVisibleLine, totalLines);
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
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.getDecorView().setSystemUiVisibility(0);
            }
        }
    }

    private void updateProgressText(int currentLine, int totalLines) {
        progressText.setText(currentLine + " of " + totalLines);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}