package com.example.audiobook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.dto.response.CategoryResponse;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryResponse> categoryResponseList;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<CategoryResponse> categoryResponseList, OnCategoryClickListener listener) {
        this.categoryResponseList = categoryResponseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Get by position
        CategoryResponse categoryResponse = categoryResponseList.get(position);
        // Bind data
        holder.textView.setText(categoryResponse.getName());
        // Click listener
        holder.textView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(categoryResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryResponseList != null ? categoryResponseList.size() : 0;
    }

    public void updateCategories(List<CategoryResponse> newCategories) {
        this.categoryResponseList = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.category_name);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryResponse categoryResponse);
    }
}