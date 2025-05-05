package com.example.audiobook.activities;

import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.audiobook.R;
import com.example.audiobook.adapters.PersonalizeAdapter;
import com.example.audiobook.dto.response.CategoryResponse;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.fragments.PersonalizeItemFragment;
import com.example.audiobook.repository.CategoryRepository;
import com.example.audiobook.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalizeActivity extends AppCompatActivity implements PersonalizeItemFragment.CategorySelectionListener {

    private ViewPager2 viewPager;
    private View[] dots;
    private PersonalizeAdapter adapter;
    private List<CategoryResponse> categories = new ArrayList<>();
    private List<CategoryResponse> selectedCategories = new ArrayList<>();
    private CategoryRepository repository;
//    private static final String USER_ID = "60409c5e-009d-41ec-b1a2-ec026000b071";
    private int maxFragmentHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        viewPager = findViewById(R.id.viewPager);
        SharedPreferences prefs = getSharedPreferences(LoginViewModel.PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        repository = new CategoryRepository(token);
        dots = new View[]{findViewById(R.id.dot1), findViewById(R.id.dot2), findViewById(R.id.dot3)};
        Button btnNext = findViewById(R.id.btnNext);

        // Set fixed widths for buttons (140dp for Next, 60dp for Skip)
        setButtonWidth(btnNext, 140);

        // Khởi tạo adapter
        adapter = new PersonalizeAdapter(this, categories);
        viewPager.setAdapter(adapter);

        // Lấy danh sách category từ API
        fetchCategories();

        // Cập nhật dots và trạng thái nút
        updateDots(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
                Button btnNext = findViewById(R.id.btnNext);
                if (position == 0) {
                    btnNext.setText("Start");
                } else if (position == 1) {
                    btnNext.setText("Next");
                } else {
                    btnNext.setText("Finish");
                }
                setButtonWidth(btnNext, 180);
                measureFragmentHeight();
            }
        });

        // Xử lý nút Next
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                saveSelectedCategories();
            }
        });

    }

    private void setButtonWidth(Button button, int dp) {
        int widthInPixels = (int) (dp * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                widthInPixels,
                (int) (66 * getResources().getDisplayMetrics().density) // Keep height consistent
        );
        params.setMarginStart((int) (10 * getResources().getDisplayMetrics().density)); // Maintain margin
        button.setLayoutParams(params);
    }

    private void fetchCategories() {
        repository.getAllCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (categories.isEmpty()) {
                        Toast.makeText(PersonalizeActivity.this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                    }
                    // Đo chiều cao sau khi cập nhật dữ liệu
                    measureFragmentHeight();
                } else {
                    Toast.makeText(PersonalizeActivity.this, "Tải danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(PersonalizeActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedCategories() {
        List<Map<String, String>> categoryList = new ArrayList<>();
        for (CategoryResponse category : selectedCategories) {
            Map<String, String> categoryMap = new HashMap<>();
            categoryMap.put("category_id", category.getId());
            categoryMap.put("category_name", category.getName());
            categoryList.add(categoryMap);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("categories", categoryList);

        repository.addRecommendCategory(requestBody).enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PersonalizeActivity.this, "Lưu danh mục thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    Toast.makeText(PersonalizeActivity.this, "Lưu danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(PersonalizeActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        startActivity(new Intent(PersonalizeActivity.this, MainActivity.class));
        finish();
    }

    private void updateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == position);
        }
    }

    private void measureFragmentHeight() {
        // Lặp qua tất cả các fragment trong ViewPager
        for (int i = 0; i < adapter.getItemCount(); i++) {
            PersonalizeItemFragment fragment = (PersonalizeItemFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + i);
            if (fragment != null && fragment.getView() != null) {
                View fragmentView = fragment.getView();
                fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Xóa listener để tránh gọi nhiều lần
                        fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int height = fragmentView.getMeasuredHeight();
                        if (height > maxFragmentHeight) {
                            maxFragmentHeight = height;
                            // Cập nhật chiều cao ViewPager2
                            ViewPager2.LayoutParams params = (ViewPager2.LayoutParams) viewPager.getLayoutParams();
                            params.height = maxFragmentHeight;
                            viewPager.setLayoutParams(params);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCategorySelected(CategoryResponse category) {
        if (!selectedCategories.contains(category)) {
            selectedCategories.add(category);
            // Cập nhật chiều cao sau khi chọn category
            measureFragmentHeight();
        }
    }

    @Override
    public void onCategoryDeselected(CategoryResponse category)  {
        selectedCategories.remove(category);
        // Cập nhật chiều cao sau khi bỏ chọn
        measureFragmentHeight();
    }

    public List<CategoryResponse> getSelectedCategories() {
        return selectedCategories;
    }
}