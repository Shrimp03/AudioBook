package com.example.audiobook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.audiobook.R;
import com.example.audiobook.adapters.OnBoardingAdapter;

public class OnBoardingActivity extends AppCompatActivity {
    private static final int[] DOT_IDS = {R.id.dot1, R.id.dot2, R.id.dot3};

    private ViewPager2 viewPager;
    private View[] dots;
    private Button btnNext;
    private Button btnSkip;
    private OnBoardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);

        initializeViews();
        setupViewPager();
        setupButtonListeners();
    }

    // Initializes UI components and dot indicators.
    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Initialize dot indicators
        dots = new View[DOT_IDS.length];
        for (int i = 0; i < DOT_IDS.length; i++) {
            dots[i] = findViewById(DOT_IDS[i]);
        }
        updateDots(0);
    }

    // Configures the ViewPager2 with the onboarding adapter and page change callback.
    private void setupViewPager() {
        adapter = new OnBoardingAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
                updateButtonState(position);
            }
        });
    }

    // Sets up click listeners for the Next and Skip buttons.
    private void setupButtonListeners() {
        btnNext.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            } else {
                navigateToLogin();
            }
        });

        btnSkip.setOnClickListener(v -> navigateToLogin());
    }

    // Updates the appearance of dot indicators based on the current page.
    private void updateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == position);
        }
    }

    // Updates the Next button's text and layout, and Skip button's visibility
    // based on the current page.
    private void updateButtonState(int position) {
        boolean isLastPage = position == adapter.getItemCount() - 1;
        btnNext.setText(isLastPage ? getString(R.string.get_started) : getString(R.string.next));
        btnSkip.setVisibility(isLastPage ? View.GONE : View.VISIBLE);

        // Adjust button width for the last page
        int buttonWidthDp = isLastPage ? 250 : 140;
        float density = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (buttonWidthDp * density + 0.5f),
                (int) (66 * density + 0.5f)
        );
        params.leftMargin = (int) (10 * density + 0.5f);
        btnNext.setLayoutParams(params);
    }

    // Navigates to the LoginActivity and closes the onboarding screen.
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}