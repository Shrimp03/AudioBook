package com.example.audiobook.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.api.APIconst;
import com.example.audiobook.models.Audiobook;
import java.util.List;

public class AudiobookAdapter extends RecyclerView.Adapter<AudiobookAdapter.ViewHolder> {
    private List<Audiobook> audiobookList;
    private OnItemClickListener listener;

    public AudiobookAdapter(List<Audiobook> audiobookList, OnItemClickListener listener) {
        this.audiobookList = audiobookList;
        this.listener = listener;
    }

    public void updateAudioBooks(List<Audiobook> newList) {
        this.audiobookList = newList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audiobook, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Audiobook audiobook = audiobookList.get(position);
        holder.itemAudioTitle.setText(audiobook.getTitle());
        String imageUrl = APIconst.BASE_URL + "/" +  audiobook.getCoverImage();
        Log.d("ImageUrl: ", imageUrl);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl) // URL từ coverImage
                .placeholder(R.drawable.home) // Hình ảnh hiển thị khi đang tải
                .error(R.drawable.home) // Hình ảnh hiển thị nếu lỗi
                .into(holder.itemAudioImg);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(audiobook));
    }

    @Override
    public int getItemCount() {
        return audiobookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemAudioTitle;
        ImageView itemAudioImg;

        public ViewHolder(View itemView) {
            super(itemView);
            itemAudioTitle = itemView.findViewById(R.id.item_audio_text);
            itemAudioImg = itemView.findViewById(R.id.item_audio_img);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Audiobook audiobook);
    }
}