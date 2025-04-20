package com.example.audiobook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.audiobook.R;
import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.response.MessageKey;
import com.example.audiobook.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    // UI components
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    // ViewModel for handling login logic
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeViewModel();
        setupListeners();
        observeLoginResult();
    }

    // Initializes UI components from the layout.
    private void initializeViews() {
        editTextUsername = findViewById(R.id.edittext_username);
        editTextPassword = findViewById(R.id.edittext_password);
        buttonLogin = findViewById(R.id.button_login);
        textViewRegister = findViewById(R.id.redirect_to_register);
    }

    // Initializes the LoginViewModel.
    private void initializeViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    // Sets up click listeners for login button and register link.
    private void setupListeners() {
        // Handle login button click
        buttonLogin.setOnClickListener(v -> handleLogin());

        // Handle register link click
        textViewRegister.setOnClickListener(v -> navigateToRegister());
    }

    // Validates input and initiates login process.
    private void handleLogin() {
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter both email and password", Toast.LENGTH_SHORT);
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        loginViewModel.loginUser(loginRequest);
    }

    // Observes login results and handles success/failure responses.
    private void observeLoginResult() {
        loginViewModel.toastMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                showToast(message, Toast.LENGTH_LONG);
                if (message.equals(MessageKey.LOGIN_USER_SUCCESSFULLY)) {
                    navigateToMain();
                }
            }
        });
    }

    // Navigates to the RegisterActivity.
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // Navigates to the MainActivity and closes the LoginActivity.
    private void navigateToMain() {
        Intent intent = new Intent(this, PersonalizeActivity.class);
        startActivity(intent);
        finish();
    }

    // Displays a toast message with the specified duration.
    private void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }
}