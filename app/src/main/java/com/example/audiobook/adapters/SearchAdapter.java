// SearchAdapter.java
package com.example.audiobook.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.api.APIconst;
import com.example.audiobook.dto.response.AudioBookResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.AudioBookViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<AudioBookResponse> audioBookResponseList;
    private OnAudioBookClickListener listener;

    public SearchAdapter(List<AudioBookResponse> audioBookList, OnAudioBookClickListener listener) {
        this.audioBookResponseList = audioBookList != null ? audioBookList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_audiobook, parent, false);
            return new AudioBookViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating ViewHolder: " + e.getMessage(), e);
            throw new RuntimeException("Failed to inflate item_audiobook layout", e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AudioBookViewHolder holder, int position) {
        try {
            AudioBookResponse audioBook = audioBookResponseList.get(position);
            if (audioBook == null) {
                Log.e(TAG, "AudioBookResponse at position " + position + " is null");
                return;
            }

            holder.itemAudioTitle.setText(audioBook.getTitle() != null ? audioBook.getTitle() : "Không có tiêu đề");
            holder.itemAudioAuthor.setText(audioBook.getAuthor() != null ? audioBook.getAuthor() : "Không có tác giả");

            String imageUrl = null;
            if (audioBook.getCoverImage() != null) {
                imageUrl = APIconst.BASE_URL + "/" + audioBook.getCoverImage();
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.home)
                    .error(R.drawable.home)
                    .into(holder.itemAudioImg);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAudioBookClick(audioBook);
                } else {
                    Log.w(TAG, "OnAudioBookClickListener is null");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error binding ViewHolder at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return audioBookResponseList.size();
    }

    public void updateAudioBooks(List<AudioBookResponse> newList) {
        try {
            this.audioBookResponseList = newList != null ? newList : new ArrayList<>();
            Log.d(TAG, "Updated audio books list with " + audioBookResponseList.size() + " items");
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error updating audio books: " + e.getMessage(), e);
        }
    }

    public static class AudioBookViewHolder extends RecyclerView.ViewHolder {
        TextView itemAudioTitle;
        TextView itemAudioAuthor;
        ImageView itemAudioImg;

        public AudioBookViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                itemAudioTitle = itemView.findViewById(R.id.item_audio_text);
                itemAudioAuthor = itemView.findViewById(R.id.item_audio_author);
                itemAudioImg = itemView.findViewById(R.id.item_audio_img);
            } catch (Exception e) {
                Log.e(TAG, "Error initializing ViewHolder views: " + e.getMessage(), e);
                throw new RuntimeException("Failed to initialize ViewHolder views", e);
            }
        }
    }

    public interface OnAudioBookClickListener {
        void onAudioBookClick(AudioBookResponse audioBookResponse);
    }
}