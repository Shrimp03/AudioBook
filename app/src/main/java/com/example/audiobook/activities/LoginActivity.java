package com.example.audiobook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.audiobook.MainActivity;
import com.example.audiobook.R;
import com.example.audiobook.helper.SessionManager;
import com.example.audiobook.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.edittext_username);
        editTextPassword = findViewById(R.id.edittext_password);
        buttonLogin = findViewById(R.id.button_login);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new LoginViewModel(new SessionManager(LoginActivity.this));
            }
        }).get(LoginViewModel.class);

        if (viewModel.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            viewModel.login(username, password);
        });

        viewModel.getLoginResult().observe(this, success -> {
            if (success != null && success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
