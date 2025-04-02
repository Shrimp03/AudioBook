package com.example.audiobook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.audiobook.MainActivity;
import com.example.audiobook.R;
import com.example.audiobook.helper.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            boolean isLoggedIn = sessionManager.isLoggedIn();
            Log.d("SplashActivity", "Is Logged In: " + isLoggedIn);
            Intent intent = new Intent(SplashActivity.this,
                    isLoggedIn ? MainActivity.class : OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}