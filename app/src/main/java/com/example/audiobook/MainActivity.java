package com.example.audiobook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.audiobook.fragments.AudiobookListFragment;
import com.example.audiobook.fragments.PlayerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_list) {
                selectedFragment = new AudiobookListFragment();
            } else if (itemId == R.id.nav_player) {
                selectedFragment = new PlayerFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AudiobookListFragment())
                .commit();
        getSupportActionBar().hide();
    }
}