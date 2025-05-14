package com.example.audiobook.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.dto.response.RatingResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.RatingViewHolder> {

    private List<RatingResponse> ratings;

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter
            .ofPattern("dd MMM yyyy, HH:mm")
            .withZone(ZoneId.systemDefault());

    public RatingListAdapter(List<RatingResponse> ratings) {
        this.ratings = ratings;
    }

    public void updateRatings(List<RatingResponse> newRatings) {
        this.ratings = newRatings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating_list, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        RatingResponse rating = ratings.get(position);
        holder.reviewerName.setText(rating.getUserResponse() != null ? rating.getUserResponse().getUsername() : "Anonymous");
        holder.reviewText.setText(rating.getComment());
        holder.ratingBar.setRating(rating.getRating() != null ? rating.getRating() : 0);

        // Xử lý createdAt (String)
        String createdAt = rating.getCreatedAt();
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
                if (dateTime != null) {
                    // Format
                    String formattedDate = OUTPUT_FORMATTER.format(dateTime);
                    holder.reviewDate.setText(formattedDate);
                } else {
                    holder.reviewDate.setText(createdAt); // Hiển thị nguyên bản nếu không parse được
                }
            } catch (Exception e) {
                Log.e("RatingListAdapter", "Error parsing createdAt: " + createdAt, e);
                holder.reviewDate.setText(createdAt);
            }
        } else {
            holder.reviewDate.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewerImage;
        TextView reviewerName;
        RatingBar ratingBar;
        TextView reviewDate;
        TextView reviewText;

        RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerImage = itemView.findViewById(R.id.reviewer_image);
            reviewerName = itemView.findViewById(R.id.reviewer_name);
            ratingBar = itemView.findViewById(R.id.review_rating);
            reviewDate = itemView.findViewById(R.id.review_date);
            reviewText = itemView.findViewById(R.id.review_text);
        }
    }
}