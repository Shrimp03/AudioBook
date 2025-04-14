package com.example.audiobook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.audiobook.R;
import java.util.HashMap;
import java.util.Map;

public class OnBoardingItemFragment extends Fragment {

    // Argument keys for Bundle
    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_DESCRIPTION = "DESC";
    private static final String ARG_IMAGE_SRC = "SRC";

    // Mapping of image source strings to drawable resource IDs
    private static final Map<String, Integer> IMAGE_RESOURCES = new HashMap<>();
    static {
        IMAGE_RESOURCES.put("on_boarding_1", R.drawable.on_boarding1);
        IMAGE_RESOURCES.put("on_boarding_2", R.drawable.on_boarding2);
        IMAGE_RESOURCES.put("on_boarding_3", R.drawable.on_boarding3);
    }

    // Creates a new instance of OnBoardingItemFragment with the given parameters.
    public static OnBoardingItemFragment newInstance(@NonNull String title, @NonNull String description, @NonNull String imageSrc) {
        OnBoardingItemFragment fragment = new OnBoardingItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_IMAGE_SRC, imageSrc);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.onboarding_item, container, false);

        // Initialize views
        TextView titleView = view.findViewById(R.id.on_title);
        TextView descriptionView = view.findViewById(R.id.on_desc);
        ImageView imageView = view.findViewById(R.id.on_item_img);

        // Populate views with data from arguments
        Bundle args = getArguments();
        if (args != null) {
            titleView.setText(args.getString(ARG_TITLE));
            descriptionView.setText(args.getString(ARG_DESCRIPTION));
            String imageSrc = args.getString(ARG_IMAGE_SRC);
            Integer resourceId = IMAGE_RESOURCES.get(imageSrc);
            if (resourceId != null) {
                imageView.setImageResource(resourceId);
            }
        }

        return view;
    }
}