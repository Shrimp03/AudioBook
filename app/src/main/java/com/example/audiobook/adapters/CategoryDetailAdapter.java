// CategoryDetailAdapter.java
package com.example.audiobook.adapters;

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

import java.util.List;

public class CategoryDetailAdapter extends RecyclerView.Adapter<CategoryDetailAdapter.AudioBookViewHolder> {
    private List<AudioBookResponse> audioBookResponseList;
    private OnAudioBookClickListener listener;

    public CategoryDetailAdapter(List<AudioBookResponse> audioBookList, OnAudioBookClickListener listener) {
        this.audioBookResponseList = audioBookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audiobook, parent, false);
        return new AudioBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioBookViewHolder holder, int position) {
        AudioBookResponse audioBook = audioBookResponseList.get(position);
        holder.itemAudioTitle.setText(audioBook.getTitle());
        String imageUrl = APIconst.BASE_URL + "/" + audioBook.getCoverImage();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.home)
                .error(R.drawable.home)
                .into(holder.itemAudioImg);
        holder.itemView.setOnClickListener(v -> listener.onAudioBookClick(audioBook));
    }

    @Override
    public int getItemCount() {
        return audioBookResponseList.size();
    }

    public void updateAudioBooks(List<AudioBookResponse> newList) {
        this.audioBookResponseList = newList;
        notifyDataSetChanged();
    }

    public static class AudioBookViewHolder extends RecyclerView.ViewHolder {
        TextView itemAudioTitle;
        ImageView itemAudioImg;

        public AudioBookViewHolder(@NonNull View itemView) {
            super(itemView);
            itemAudioTitle = itemView.findViewById(R.id.item_audio_text);
            itemAudioImg = itemView.findViewById(R.id.item_audio_img);
        }
    }

    public interface OnAudioBookClickListener {
        void onAudioBookClick(AudioBookResponse audioBookResponse);
    }
}