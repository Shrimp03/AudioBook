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

    private ViewPager2 viewPager;
    private View[] dots;
    private OnBoardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);

        viewPager = findViewById(R.id.viewPager);
        adapter = new OnBoardingAdapter(this);
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

                if (position == adapter.getItemCount() - 1) {
                    btnNext.setText("Get Started");
                    btnSkip.setVisibility(View.GONE);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) (250 * getResources().getDisplayMetrics().density + 0.5f),
                            (int) (66 * getResources().getDisplayMetrics().density + 0.5f)
                    );
                    params.leftMargin = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                    btnNext.setLayoutParams(params);
                } else {
                    btnNext.setText("Next");
                    btnSkip.setVisibility(View.VISIBLE);

                    // Reset to original width
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            (int) (140 * getResources().getDisplayMetrics().density + 0.5f),
                            (int) (66 * getResources().getDisplayMetrics().density + 0.5f)
                    );
                    params.leftMargin = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                    btnNext.setLayoutParams(params);
                }
            }
        });

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < adapter.getItemCount() - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Xử lý nút Skip
        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnBoardingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateDots(int position) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == position);
        }
    }
}