package com.example.audiobook.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.function.Consumer;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.AudioBookViewHolder> {
    private static final String TAG = "LibraryAdapter";
    private List<AudioBookResponse> audioBookResponseList;
    private final OnAudioBookClickListener onItemClickListener; // Callback cho nhấn item
    private final Consumer<AudioBookResponse> onEditClickListener; // Callback cho nhấn "Cập nhật"
    private final Consumer<AudioBookResponse> onDeleteClickListener; // Callback cho nhấn "Xóa"

    public LibraryAdapter(List<AudioBookResponse> audioBookList, OnAudioBookClickListener onItemClickListener,
                          Consumer<AudioBookResponse> onEditClickListener, Consumer<AudioBookResponse> onDeleteClickListener) {
        this.audioBookResponseList = audioBookList != null ? audioBookList : new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
        this.onEditClickListener = onEditClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_audiobook, parent, false); // Sử dụng item_audio_book.xml
            return new AudioBookViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating ViewHolder: " + e.getMessage(), e);
            throw new RuntimeException("Failed to inflate item_audio_book layout", e);
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
                imageUrl =  audioBook.getCoverImage();
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.home)
                    .error(R.drawable.home)
                    .into(holder.itemAudioImg);

            // Sự kiện nhấn vào item
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onAudioBookClick(audioBook);
                } else {
                    Log.w(TAG, "OnAudioBookClickListener is null");
                }
            });

            // Sự kiện nhấn biểu tượng "Cập nhật"
            holder.iconEdit.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.accept(audioBook);
                } else {
                    Log.w(TAG, "OnEditClickListener is null");
                }
            });

            // Sự kiện nhấn biểu tượng "Xóa"
            holder.iconDelete.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.accept(audioBook);
                } else {
                    Log.w(TAG, "OnDeleteClickListener is null");
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
        ImageButton iconEdit; // Biểu tượng "Cập nhật"
        ImageButton iconDelete; // Biểu tượng "Xóa"

        public AudioBookViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                itemAudioTitle = itemView.findViewById(R.id.item_audio_text);
                itemAudioAuthor = itemView.findViewById(R.id.item_audio_author);
                itemAudioImg = itemView.findViewById(R.id.item_audio_img);
                iconEdit = itemView.findViewById(R.id.icon_edit);
                iconDelete = itemView.findViewById(R.id.icon_delete);
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