package com.example.audiobook.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.AudiobookAdapter;
import com.example.audiobook.adapters.CategoryAdapter;
import com.example.audiobook.models.ApiResponse;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.models.Category;
import com.example.audiobook.repository.AudiobookRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView categoryRecyclerView;
    private RecyclerView recommendAudioBookRecycleView;
    private CategoryAdapter categoryAdapter;
    private AudiobookAdapter audiobookAdapter;
    private AudiobookRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view);
        recommendAudioBookRecycleView = view.findViewById(R.id.recommend_recycler_view);
        recommendAudioBookRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            // Gọi sang Fragment hiển thị audiobook theo category
            Bundle bundle = new Bundle();
            bundle.putString("categoryId", category.getId());
            bundle.putString("categoryName", category.getName());

            CategoryDetailFragment detailFragment = new CategoryDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment) // thay bằng container thực tế của bạn
                    .addToBackStack(null)
                    .commit();
        });
        audiobookAdapter = new AudiobookAdapter(new ArrayList<>(), new AudiobookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Audiobook audiobook) {

            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
        recommendAudioBookRecycleView.setAdapter(audiobookAdapter);

        // Gọi API
        repository = new AudiobookRepository();
        fetchCategories();
        fetchAudioBooks();
        return view;
    }

    private void fetchCategories() {
        repository.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    Log.d(TAG, "Categories loaded: " + categories.size());
                    categoryAdapter.updateCategories(categories);
                    categoryRecyclerView.requestLayout();
                } else {
                    Log.e(TAG, "Failed to load categories. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "API error: " + t.getMessage());
            }
        });
    }

    private void fetchAudioBooks() {
        repository.getAllAudioBooks().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Audiobook> audiobooks = response.body().getData().getContent();
                    Log.d(TAG, "AudioBooks loaded: " + audiobooks.size());
                    audiobookAdapter.updateAudioBooks(audiobooks);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "API error: " + t.getMessage());
            }
        });
    }
}