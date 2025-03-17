package com.example.audiobook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.models.Audiobook;
import java.util.List;

public class AudiobookAdapter extends RecyclerView.Adapter<AudiobookAdapter.ViewHolder> {
    private List<Audiobook> audiobookList;
    private OnItemClickListener listener;

    public AudiobookAdapter(List<Audiobook> audiobookList, OnItemClickListener listener) {
        this.audiobookList = audiobookList;
        this.listener = listener;
    }

    public void updateList(List<Audiobook> newList) {
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
        holder.titleTextView.setText(audiobook.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(audiobook));
    }

    @Override
    public int getItemCount() {
        return audiobookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textview_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Audiobook audiobook);
    }
}