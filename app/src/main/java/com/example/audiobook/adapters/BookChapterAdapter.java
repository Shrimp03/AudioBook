package com.example.audiobook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.example.audiobook.dto.response.BookChapterResponse;

import java.util.List;

public class BookChapterAdapter extends RecyclerView.Adapter<BookChapterAdapter.BookChapterViewHolder> {

    private List<BookChapterResponse> bookChapters;
    private OnChapterClickListener listener;

    public BookChapterAdapter(List<BookChapterResponse> bookChapterResponses, OnChapterClickListener listener) {
        this.bookChapters = bookChapterResponses;
        this.listener = listener;
    }

    @Override
    public BookChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new BookChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookChapterViewHolder holder, int position) {
        // Get by position
        BookChapterResponse bookChapterResponse = bookChapters.get(position);
        // Bind data
        holder.chapterNumber.setText(bookChapterResponse.getChapterNumber().toString());
        holder.chapterTitle.setText(bookChapterResponse.getTitle());
        // Click listener
        holder.itemView.setOnClickListener(v -> listener.onChapterClick(bookChapterResponse));
    }

    @Override
    public int getItemCount() {
        return bookChapters.size();
    }

    public void updateBookChapters(List<BookChapterResponse> newList) {
        this.bookChapters = newList;
        notifyDataSetChanged();
    }

    static class BookChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterNumber;
        TextView chapterTitle;

        public BookChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterNumber = itemView.findViewById(R.id.chapter_number);
            chapterTitle = itemView.findViewById(R.id.chapter_title);
        }
    }

    public interface OnChapterClickListener {
        void onChapterClick(BookChapterResponse bookChapterResponse);
    }
}