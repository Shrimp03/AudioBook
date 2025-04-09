package com.example.audiobook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.audiobook.MainActivity;
import com.example.audiobook.R;
import com.example.audiobook.adapters.PersonalizeAdapter;
import com.example.audiobook.fragments.PersonalizeItemFragment;
import com.example.audiobook.models.Category;
import java.util.ArrayList;
import java.util.List;

public class PersonalizeActivity extends AppCompatActivity implements PersonalizeItemFragment.CategorySelectionListener {

    private ViewPager2 viewPager;
    private View[] dots;
    private PersonalizeAdapter adapter;
    private List<Category> selectedCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        viewPager = findViewById(R.id.viewPager);
        selectedCategories = new ArrayList<>();

        // Tạo dữ liệu mẫu
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Thể thao"));
        categories.add(new Category(2, "Âm nhạc"));
        categories.add(new Category(3, "Công nghệ"));
        categories.add(new Category(4, "Du lịch"));
        categories.add(new Category(5, "Sách"));
        categories.add(new Category(6, "Phim"));

        // Kiểm tra danh sách categories
        if (categories.isEmpty()) {
            throw new IllegalStateException("Categories list is empty!");
        }

        adapter = new PersonalizeAdapter(this, categories);
        viewPager.setAdapter(adapter);

        dots = new View[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3)
        };

        updateDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);

                Button btnNext = findViewById(R.id.btnNext);
                Button btnSkip = findViewById(R.id.btnSkip);

                if (position == 0) {
                    btnNext.setText("Start");
                    btnSkip.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    btnNext.setText("Next");
                    btnSkip.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    btnNext.setText("Finish");
                    btnSkip.setVisibility(View.GONE);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) (250 * getResources().getDisplayMetrics().density + 0.5f),
                            (int) (66 * getResources().getDisplayMetrics().density + 0.5f)
                    );
                    params.leftMargin = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                    btnNext.setLayoutParams(params);
                }
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                // Xử lý lưu danh mục đã chọn (ví dụ: gửi lên server hoặc lưu local)
                Intent intent = new Intent(PersonalizeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnSkip).setOnClickListener(v -> {
            Intent intent = new Intent(PersonalizeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == position);
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        if (!selectedCategories.contains(category)) {
            selectedCategories.add(category);
        }
    }

    @Override
    public void onCategoryDeselected(Category category) {
        selectedCategories.remove(category);
    }

    public List<Category> getSelectedCategories() {
        return selectedCategories;
    }
}