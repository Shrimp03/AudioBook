package com.example.audiobook.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.audiobook.R;

public class PlayerFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private Button playButton, pauseButton;
    private TextView titleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        titleTextView = view.findViewById(R.id.textview_title);
        playButton = view.findViewById(R.id.button_play);
        pauseButton = view.findViewById(R.id.button_pause);

        String filePath;
        String title;

        // Kiểm tra xem có Bundle từ AudiobookListFragment không
        if (getArguments() != null) {
            filePath = getArguments().getString("filePath");
            title = getArguments().getString("title", "Unknown Title");
        } else {
            // Nếu không có Bundle, dùng file mẫu trong res/raw
            filePath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.sample_audio;
            title = "Sample Audio";
        }

        titleTextView.setText(title);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading audio", Toast.LENGTH_SHORT).show();
        }

        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


