package com.example.audiobook.fragments;

import android.app.Dialog;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.api.APIconst;

import java.io.IOException;
import java.util.Locale;

public class PlayerFragment extends DialogFragment {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable updateSeekBarRunnable;

    // UI elements
    private TextView headerTitle;
    private ImageView bookCover;
    private TextView bookTitle;
    private TextView bookAuthor;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalDuration;
    private ImageButton playPauseButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the style to fullscreen
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        headerTitle = view.findViewById(R.id.header_title);
        bookCover = view.findViewById(R.id.book_cover);
        bookTitle = view.findViewById(R.id.book_title);
        bookAuthor = view.findViewById(R.id.book_author);
        seekBar = view.findViewById(R.id.seek_bar);
        currentTime = view.findViewById(R.id.current_time);
        totalDuration = view.findViewById(R.id.total_duration);
        playPauseButton = view.findViewById(R.id.play_pause_button);

        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);
        ImageButton volumeButton = view.findViewById(R.id.volume_button);
        ImageButton rewindButton = view.findViewById(R.id.rewind_button);
        ImageButton forwardButton = view.findViewById(R.id.forward_button);
        ImageButton shareButton = view.findViewById(R.id.share_button);
        TextView bookmarkButton = view.findViewById(R.id.bookmark_button);
        TextView chapterButton = view.findViewById(R.id.chapter_button);
        TextView speedButton = view.findViewById(R.id.speed_button);
        TextView downloadButton = view.findViewById(R.id.download_button);

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String author = arguments.getString("author");
            String coverImage = arguments.getString("coverImage");
            String maleAudioUrl = arguments.getString("maleAudioUrl");

            // Map data to UI elements
            if (title != null) {
                bookTitle.setText(title);
                if (title.length() > 20) {
                    headerTitle.setText(title.substring(0, 17) + "...");
                } else {
                    headerTitle.setText(title);
                }
            }
            if (author != null) {
                bookAuthor.setText(author);
            }
            if (coverImage != null && getContext() != null) {
                String imageUrl = APIconst.BASE_URL + "/" + coverImage;
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_book)
                        .error(R.drawable.placeholder_book)
                        .into(bookCover);
            }
            if (maleAudioUrl != null) {
                playAudio(APIconst.BASE_URL + "/" + maleAudioUrl);
            }
        }

        // Set click listeners
        backButton.setOnClickListener(v -> dismiss());

        menuButton.setOnClickListener(v -> showToast("Menu clicked"));
        volumeButton.setOnClickListener(v -> showToast("Volume control not implemented"));
        shareButton.setOnClickListener(v -> showToast("Share not implemented"));
        bookmarkButton.setOnClickListener(v -> showToast("Bookmark not implemented"));
        chapterButton.setOnClickListener(v -> showToast("Chapter selection not implemented"));
        speedButton.setOnClickListener(v -> showToast("Speed adjustment not implemented"));
        downloadButton.setOnClickListener(v -> showToast("Download not implemented"));

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                pauseAudio();
            } else {
                resumeAudio();
            }
        });

        rewindButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() - 10000; // Rewind 10 seconds
                mediaPlayer.seekTo(Math.max(newPosition, 0));
                updateSeekBar();
            }
        });

        forwardButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int newPosition = mediaPlayer.getCurrentPosition() + 10000; // Forward 10 seconds
                mediaPlayer.seekTo(Math.min(newPosition, mediaPlayer.getDuration()));
                updateSeekBar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (handler != null) {
                    handler.removeCallbacks(updateSeekBarRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isPlaying) {
                    startSeekBarUpdates();
                }
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

    private void playAudio(String audioUrl) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );

                mediaPlayer.setOnPreparedListener(mp -> {
                    seekBar.setMax(mediaPlayer.getDuration());
                    totalDuration.setText(formatTime(mediaPlayer.getDuration()));
                    mediaPlayer.start();
                    isPlaying = true;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    startSeekBarUpdates();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                    currentTime.setText(formatTime(0));
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    showToast("Error playing audio");
                    isPlaying = false;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    releaseMediaPlayer();
                    return true;
                });
            }

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            showToast("Failed to play audio");
            releaseMediaPlayer();
        }
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
                isPlaying = true;
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                startSeekBarUpdates();
            } catch (IllegalStateException e) {
                showToast("Error resuming audio");
                releaseMediaPlayer();
            }
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPlaying = false;
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                if (handler != null) {
                    handler.removeCallbacks(updateSeekBarRunnable);
                }
            } catch (IllegalStateException e) {
                showToast("Error pausing audio");
                releaseMediaPlayer();
            }
        }
    }

    private void startSeekBarUpdates() {
        handler = new Handler(Looper.getMainLooper());
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int position = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(position);
                    currentTime.setText(formatTime(position));
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(updateSeekBarRunnable, 1000);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            int position = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(position);
            currentTime.setText(formatTime(position));
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                // Ignore
            } finally {
                mediaPlayer = null;
            }
        }
        isPlaying = false;
        if (playPauseButton != null) {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
        if (handler != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}

