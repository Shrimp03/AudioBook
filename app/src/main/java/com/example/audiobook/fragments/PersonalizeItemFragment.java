package com.example.audiobook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.CategoryItemAdapter;
import com.example.audiobook.dto.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class PersonalizeItemFragment extends Fragment {
    private static final String ARG_TYPE = "type";
    private static final String ARG_SUBTITLE = "subtitle";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_CATEGORIES = "categories";

    public static final int TYPE_INTRO = 0;
    public static final int TYPE_CATEGORIES = 1;
    public static final int TYPE_FINISH = 2;

    private int type;
    private String subtitle;
    private String title;
    private String description;
    private List<CategoryResponse> categories;
    private CategorySelectionListener selectionListener;

    public interface CategorySelectionListener {
        void onCategorySelected(CategoryResponse categoryResponse);
        void onCategoryDeselected(CategoryResponse categoryResponse);
    }

    public static PersonalizeItemFragment newInstance(int type, String subtitle, String title, String description, List<CategoryResponse> categories) {
        PersonalizeItemFragment fragment = new PersonalizeItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_SUBTITLE, subtitle);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putSerializable(ARG_CATEGORIES, new ArrayList<>(categories));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            subtitle = getArguments().getString(ARG_SUBTITLE);
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESCRIPTION);
            categories = (List<CategoryResponse>) getArguments().getSerializable(ARG_CATEGORIES);
        }
        if (getActivity() instanceof CategorySelectionListener) {
            selectionListener = (CategorySelectionListener) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personalize_item, container, false);

        ImageView finishImage = view.findViewById(R.id.finishImage);
        TextView introSubtitleView = view.findViewById(R.id.introSubtitle);
        TextView introTitleView = view.findViewById(R.id.introTitle);
        TextView subtitleView = view.findViewById(R.id.subtitle);
        TextView titleView = view.findViewById(R.id.title);
        TextView descriptionView = view.findViewById(R.id.description);
        TextView finishDescriptionView = view.findViewById(R.id.finishDescription);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Kiểm tra dữ liệu
        if (title == null || description == null || categories == null) {
            titleView.setText("Error: Data not loaded");
            return view;
        }

        switch (type) {
            case TYPE_INTRO:
                finishImage.setVisibility(View.GONE);
                introSubtitleView.setVisibility(View.VISIBLE);
                introTitleView.setVisibility(View.VISIBLE);
                subtitleView.setVisibility(View.GONE);
                titleView.setVisibility(View.GONE);
                descriptionView.setVisibility(View.VISIBLE);
                finishDescriptionView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                introSubtitleView.setText(subtitle);
                introTitleView.setText(title);
                descriptionView.setText(description);
                break;
            case TYPE_CATEGORIES:
                finishImage.setVisibility(View.GONE);
                introSubtitleView.setVisibility(View.GONE);
                introTitleView.setVisibility(View.GONE);
                subtitleView.setVisibility(View.GONE);
                titleView.setVisibility(View.VISIBLE);
                descriptionView.setVisibility(View.GONE);
                finishDescriptionView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                titleView.setText(title);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new CategoryItemAdapter(categories, selectionListener));
                break;
            case TYPE_FINISH:
                finishImage.setVisibility(View.VISIBLE);
                introSubtitleView.setVisibility(View.GONE);
                introTitleView.setVisibility(View.GONE);
                subtitleView.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.VISIBLE);
                descriptionView.setVisibility(View.GONE);
                finishDescriptionView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                subtitleView.setText(subtitle);
                titleView.setText(title);
                finishDescriptionView.setText(description);
                break;
            default:
                titleView.setText("Unknown Type");
                break;
        }

        return view;
    }
}