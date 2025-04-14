package com.example.audiobook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.activities.PersonalizeActivity;
import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.fragments.PersonalizeItemFragment;

import java.util.List;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.ViewHolder> {
    private List<CategoryResponse> categories;
    private PersonalizeItemFragment.CategorySelectionListener selectionListener;

    public CategoryItemAdapter(List<CategoryResponse> categories, PersonalizeItemFragment.CategorySelectionListener listener) {
        this.categories = categories;
        this.selectionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_personal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get by position
        CategoryResponse categoryResponse = categories.get(position);
        // Bind data
        holder.categoryName.setText(categoryResponse.getName());
        // Get check status
        holder.checkBox.setChecked(((PersonalizeActivity) holder.itemView.getContext()).getSelectedCategories().contains(categoryResponse));
        // Check listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectionListener.onCategorySelected(categoryResponse);
            } else {
                selectionListener.onCategoryDeselected(categoryResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}