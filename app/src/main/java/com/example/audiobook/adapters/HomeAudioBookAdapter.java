package com.example.audiobook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.api.APIconst;
import com.example.audiobook.dto.response.AudioBookResponse;
import java.util.List;

public class HomeAudioBookAdapter extends RecyclerView.Adapter<HomeAudioBookAdapter.AudioBookViewHolder> {
    private List<AudioBookResponse> audioBookResponseList;
    private OnAudioBookClickListener listener;

    public HomeAudioBookAdapter(List<AudioBookResponse> audioBookList, OnAudioBookClickListener listener) {
        this.audioBookResponseList = audioBookList;
        this.listener = listener;
    }

    @Override
    public AudioBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audiobook, parent, false);
        return new AudioBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioBookViewHolder holder, int position) {
        // Get by position
        AudioBookResponse audioBook = audioBookResponseList.get(position);
        // Bind data
        holder.itemAudioTitle.setText(audioBook.getTitle());
        // Bind image
        String imageUrl = APIconst.BASE_URL + "/" +  audioBook.getCoverImage();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // URL từ coverImage
                .placeholder(R.drawable.home) // Hình ảnh hiển thị khi đang tải
                .error(R.drawable.home) // Hình ảnh hiển thị nếu lỗi
                .into(holder.itemAudioImg);
        // Click listener
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

        public AudioBookViewHolder(View itemView) {
            super(itemView);
            itemAudioTitle = itemView.findViewById(R.id.item_audio_text);
            itemAudioImg = itemView.findViewById(R.id.item_audio_img);
        }
    }

    public interface OnAudioBookClickListener {
        void onAudioBookClick(AudioBookResponse audioBookResponse);
    }
}