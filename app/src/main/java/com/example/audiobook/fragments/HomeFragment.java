package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.CategoryAdapter;
import com.example.audiobook.models.Category;
import com.example.audiobook.repository.AudiobookRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment"; // Tag cho Logcat
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private AudiobookRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Khởi tạo repository và gọi API
        repository = new AudiobookRepository();
        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        Log.d(TAG, "Starting to fetch categories...");
        repository.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "API call successful. Response code: " + response.code());
                    if (response.body() != null) {
                        Log.d(TAG, "Categories loaded: " + response.body().size() + " items");
                        categoryAdapter.updateCategories(response.body());
                    } else {
                        Log.e(TAG, "Response body is null");
                        Toast.makeText(getContext(), "Failed to load categories: Response body is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API call failed. Response code: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Could not parse error body: " + e.getMessage());
                    }
                    Toast.makeText(getContext(), "Failed to load categories. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "API call failed with error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}