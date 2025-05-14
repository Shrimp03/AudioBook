package com.example.audiobook.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.audiobook.R;
import com.example.audiobook.fragments.HomeFragment;
import com.example.audiobook.fragments.LibraryFragment;
import com.example.audiobook.fragments.PlayerFragment;
import com.example.audiobook.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.analytics.FirebaseAnalytics;


// Main activity hosting the app's primary navigation and fragments
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAnalytics mFirebaseAnalytics;


    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFCMToken();

        // Initialize environment configuration
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        // Initialize BottomNavigationView and set navigation listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            switchFragment(selectedFragment);
            return true;
        });

        // Set HomeFragment as the default fragment
        switchFragment(new HomeFragment());
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and display the token
                    Log.d(TAG, "FCM Token: " + token);
//                    Toast.makeText(MainActivity.this, "FCM Token: " + token, Toast.LENGTH_LONG).show();
                });
    }


    // Returns the appropriate fragment based on the selected navigation item
    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        } else if (itemId == R.id.nav_search) {
            return new SearchFragment();
        }
        else {
            return new LibraryFragment();
        }
    }

    // Switches to the specified fragment if it is not null
    private void switchFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}