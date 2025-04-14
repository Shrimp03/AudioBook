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

public class CategoryDetailFragment extends Fragment {

    private String categoryId;

    private String categoryName;

    ImageView imageViewBack;

    TextView textViewBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            categoryName = getArguments().getString("categoryName");
        }

        imageViewBack = view.findViewById(R.id.detail_back);
        textViewBack = view.findViewById(R.id.detail_category_title);
        imageViewBack.setOnClickListener(v -> requireActivity().onBackPressed());
        textViewBack.setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }


}

