package com.example.audiobook.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;

public class PlayerFragment extends DialogFragment {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable updateSeekBarRunnable;
    private float playbackSpeed = 1.0f;
    private boolean isMuted = false; // Track mute state
    private boolean isMaleVoice = true; // Track current voice (male or female)
    private String maleAudioUrl;
    private String femaleAudioUrl;
    private TextView downloadButton;
    private boolean isDownloading = false; // Track download state
    private long downloadId = -1; // Track download ID
    private DownloadManager downloadManager;
    private BroadcastReceiver downloadReceiver;

    // UI elements
    private TextView headerTitle;
    private ImageView bookCover;
    private TextView bookTitle;
    private TextView bookAuthor;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalDuration;
    private ImageButton playPauseButton;
    private TextView speedButton;
    private TextView changeVoiceButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        speedButton = view.findViewById(R.id.speed_button);
        changeVoiceButton = view.findViewById(R.id.change_voice_button);
        downloadButton = view.findViewById(R.id.download_button);

        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView menuButton = view.findViewById(R.id.menu_button);
        ImageButton volumeButton = view.findViewById(R.id.volume_button);
        ImageButton rewindButton = view.findViewById(R.id.rewind_button);
        ImageButton forwardButton = view.findViewById(R.id.forward_button);
        ImageButton shareButton = view.findViewById(R.id.share_button);

        // Initialize DownloadManager
        downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        // Set up download completion receiver
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        int statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (statusColumn != -1) {
                            int status = cursor.getInt(statusColumn);
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                showToast("Download completed");
                            } else {
                                showToast("Download failed");
                            }
                        } else {
                            showToast("Error checking download status");
                        }
                    } else {
                        showToast("Download status not found");
                    }
                    cursor.close();
                    // Reset download state and UI
                    isDownloading = false;
                    if (downloadButton != null) {
                        downloadButton.setText("Download");
                        downloadButton.setEnabled(true);
                    }
                    downloadId = -1; // Reset downloadId
                }
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getContext().registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            getContext().registerReceiver(downloadReceiver, filter);
        }

        // Extract data from the Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            String title = arguments.getString("title");
            String author = arguments.getString("author");
            String coverImage = arguments.getString("coverImage");
            maleAudioUrl = arguments.getString("maleAudioUrl");
            femaleAudioUrl = arguments.getString("femaleAudioUrl");

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
                String imageUrl =  coverImage;
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_book)
                        .error(R.drawable.placeholder_book)
                        .into(bookCover);
            }
            if (maleAudioUrl != null) {
                playAudio( maleAudioUrl);
                isMaleVoice = true;
                changeVoiceButton.setText("Male Voice");
            }
        }

        // Set click listeners
        backButton.setOnClickListener(v -> dismiss());

        menuButton.setOnClickListener(v -> showToast("Menu clicked"));

        volumeButton.setOnClickListener(v -> adjustVolume());

        shareButton.setOnClickListener(v -> shareAudiobook());

        changeVoiceButton.setOnClickListener(v -> changeVoice());

        speedButton.setOnClickListener(v -> changePlaybackSpeed());

        downloadButton.setOnClickListener(v -> downloadAudio());

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
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.getDecorView().setSystemUiVisibility(0);
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
                    // Apply playback speed
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed));
                    // Apply mute state
                    mediaPlayer.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);
                    mediaPlayer.start();
                    isPlaying = true;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    startSeekBarUpdates();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    playPauseButton.setImageResource(R.drawable.ic_play_2);
                    mediaPlayer.seekTo(0);
                    seekBar.setProgress(0);
                    currentTime.setText(formatTime(0));
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    showToast("Error playing audio");
                    isPlaying = false;
                    playPauseButton.setImageResource(R.drawable.ic_play_2);
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
                playPauseButton.setImageResource(R.drawable.ic_play_2);
                if (handler != null) {
                    handler.removeCallbacks(updateSeekBarRunnable);
                }
            } catch (IllegalStateException e) {
                showToast("Error pausing audio");
                releaseMediaPlayer();
            }
        }
    }

    private void adjustVolume() {
        if (mediaPlayer != null) {
            // Toggle between mute and full volume
            if (isMuted) {
                mediaPlayer.setVolume(1f, 1f);
                isMuted = false;
                showToast("Volume restored");
            } else {
                mediaPlayer.setVolume(0f, 0f);
                isMuted = true;
                showToast("Muted");
            }
        }
    }

    private void shareAudiobook() {
        String title = bookTitle.getText().toString();
        String author = bookAuthor.getText().toString();
        String shareText = "Check out this audiobook: " + title + " by " + author;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Audiobook");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Audiobook"));
    }

    private void changeVoice() {
        if (mediaPlayer != null && maleAudioUrl != null && femaleAudioUrl != null) {
            // Store current play state and position
            boolean wasPlaying = isPlaying;
            int currentPosition = mediaPlayer.getCurrentPosition();

            // Pause if playing
            if (wasPlaying) {
                pauseAudio();
            }

            // Switch voice
            isMaleVoice = !isMaleVoice;
            String newAudioUrl = isMaleVoice ?  maleAudioUrl :  femaleAudioUrl;
            changeVoiceButton.setText(isMaleVoice ? "Male Voice" : "Female Voice");

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(newAudioUrl);
                mediaPlayer.setOnPreparedListener(mp -> {
                    seekBar.setMax(mediaPlayer.getDuration());
                    totalDuration.setText(formatTime(mediaPlayer.getDuration()));
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed));
                    mediaPlayer.setVolume(isMuted ? 0f : 1f, isMuted ? 0f : 1f);
                    mediaPlayer.seekTo(currentPosition);
                    if (wasPlaying) {
                        mediaPlayer.start();
                        isPlaying = true;
                        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                        startSeekBarUpdates();
                    } else {
                        isPlaying = false;
                        playPauseButton.setImageResource(R.drawable.ic_play_2);
                        if (handler != null) {
                            handler.removeCallbacks(updateSeekBarRunnable);
                        }
                    }
                });
                mediaPlayer.prepareAsync();
                showToast("Switched to " + (isMaleVoice ? "male" : "female") + " voice");
            } catch (IOException e) {
                showToast("Failed to switch voice");
                releaseMediaPlayer();
            }
        } else {
            showToast("Voice URLs not available");
        }
    }

    private void changePlaybackSpeed() {
        if (mediaPlayer != null) {
            // Store current play state and position
            boolean wasPlaying = isPlaying;
            int currentPosition = mediaPlayer.getCurrentPosition();

            // Pause to avoid unintended playback
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }

            // Cycle through speeds: 0.5x, 1.0x, 1.5x, 2.0x
            float[] speeds = {0.5f, 1.0f, 1.5f, 2.0f};
            int currentIndex = 0;
            for (int i = 0; i < speeds.length; i++) {
                if (playbackSpeed == speeds[i]) {
                    currentIndex = i;
                    break;
                }
            }
            playbackSpeed = speeds[(currentIndex + 1) % speeds.length];

            try {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(playbackSpeed));
                speedButton.setText("Speed " + String.format(Locale.getDefault(), "%.1fx", playbackSpeed));
                showToast("Playback speed set to " + String.format(Locale.getDefault(), "%.1fx", playbackSpeed));

                // Ensure correct state after setting speed
                if (wasPlaying) {
                    mediaPlayer.seekTo(currentPosition);
                    mediaPlayer.start();
                    isPlaying = true;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    startSeekBarUpdates();
                } else {
                    // Explicitly pause and ensure UI reflects paused state
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    mediaPlayer.seekTo(currentPosition);
                    isPlaying = false;
                    playPauseButton.setImageResource(R.drawable.ic_play_2);
                    if (handler != null) {
                        handler.removeCallbacks(updateSeekBarRunnable);
                    }
                }
            } catch (IllegalStateException e) {
                showToast("Error changing playback speed");
                // Restore paused state if error occurs
                if (!wasPlaying) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    isPlaying = false;
                    playPauseButton.setImageResource(R.drawable.ic_play_2);
                    if (handler != null) {
                        handler.removeCallbacks(updateSeekBarRunnable);
                    }
                }
            }
        }
    }

    private void downloadAudio() {
        if (isDownloading) {
            showToast("Download in progress");
            return;
        }

        String audioUrl = isMaleVoice ?  maleAudioUrl :  femaleAudioUrl;
        if (audioUrl == null || audioUrl.isEmpty()) {
            showToast("No audio URL available");
            return;
        }

        try {
            Uri uri = Uri.parse(audioUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            String title = bookTitle.getText().toString();
            String fileName = title + "_" + (isMaleVoice ? "male" : "female") + ".mp3";
            request.setTitle("Downloading " + title);
            request.setDescription("Downloading audiobook audio");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            downloadId = downloadManager.enqueue(request);
            isDownloading = true;
            downloadButton.setText("Downloading...");
            downloadButton.setEnabled(false);
            showToast("Download started");
        } catch (Exception e) {
            showToast("Failed to start download");
            isDownloading = false;
            downloadButton.setText("Download");
            downloadButton.setEnabled(true);
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
            playPauseButton.setImageResource(R.drawable.ic_play_2);
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
        if (downloadReceiver != null && getContext() != null) {
            try {
                getContext().unregisterReceiver(downloadReceiver);
            } catch (IllegalArgumentException e) {
                // Ignore if receiver is not registered
            }
        }
    }
}