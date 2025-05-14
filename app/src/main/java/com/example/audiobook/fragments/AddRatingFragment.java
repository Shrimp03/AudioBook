package com.example.audiobook.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.audiobook.R;
import com.example.audiobook.viewmodel.AudioBookDetailViewModel;
import com.example.audiobook.viewmodel.LoginViewModel;

import java.util.UUID;

public class AddRatingFragment extends DialogFragment {

    private RatingBar ratingBar;
    private EditText commentEditText;
    private TextView charCountText;
    private Button submitButton;
    private AudioBookDetailViewModel viewModel;
    private LoginViewModel loginViewModel;
    private String audioBookId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(AudioBookDetailViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        audioBookId = getArguments() != null ? getArguments().getString("audioBookId") : null;

        setupListeners();
    }

    private void initializeViews(View view) {
        ratingBar = view.findViewById(R.id.rating_bar);
        commentEditText = view.findViewById(R.id.comment_edit_text);
        charCountText = view.findViewById(R.id.char_count_text);
        submitButton = view.findViewById(R.id.submit_button);
    }

    private void setupListeners() {
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCountText.setText(length + "/75");
                submitButton.setEnabled(length <= 75 && ratingBar.getRating() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            submitButton.setEnabled(rating > 0 && commentEditText.getText().length() <= 75);
        });

        submitButton.setOnClickListener(v -> {
            String token = loginViewModel.getSavedToken();
            if (token == null) {
                Toast.makeText(getContext(), "Please log in to submit a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = decodeUserIdFromToken(token);
            if (userId == null) {
                Toast.makeText(getContext(), "Invalid token", Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = commentEditText.getText().toString().trim();
            int rating = (int) ratingBar.getRating();

            viewModel.submitRating(UUID.fromString(audioBookId), UUID.fromString(userId), rating, comment, token);
            viewModel.ratingResult.observe(getViewLifecycleOwner(), result -> {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                if (result.startsWith("Rating submitted")) {
                    viewModel.fetchRatings(UUID.fromString(audioBookId));
                    dismiss();
                }
            });
        });
    }

    private String decodeUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT));
            // Giả sử payload chứa "sub" hoặc "user_id" là UUID
            String userId = payload.split("\"uid\":\"")[1].split("\"")[0];
            return userId;
        } catch (Exception e) {
            return null;
        }
    }
}