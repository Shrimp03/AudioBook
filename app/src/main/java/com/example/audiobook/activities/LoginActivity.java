package com.example.audiobook.activities;

import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.repository.CategoryRepository;
import com.example.audiobook.viewmodel.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // UI components
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    // ViewModel for handling login logic
    private LoginViewModel loginViewModel;
    private CategoryRepository categoryRepository;

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
                    SharedPreferences prefs = getSharedPreferences(LoginViewModel.PREFS_NAME, MODE_PRIVATE);
                    String token = prefs.getString(TOKEN_KEY, null);
                    if(token != null){
                        navigateToMain(token);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Lỗi khi không có token: " + token, Toast.LENGTH_SHORT).show();
                    }
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
    private void navigateToMain(String token) {

        categoryRepository = new CategoryRepository(token);

        // Gọi checkFirstLogin để kiểm tra trạng thái firstLogin
        categoryRepository.checkFirstLogin().enqueue(new Callback<ResponseObject>() {
            @Override
            public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseObject responseObject = response.body();
                    if ("success".equals(responseObject.getStatus()) && responseObject.getData() instanceof Boolean) {
                        boolean hasFavorites = (Boolean) responseObject.getData();
                        Intent intent;
                        if (hasFavorites) {
                            // Không phải firstLogin, chuyển đến MainActivity
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        } else {
                            // Là firstLogin, chuyển đến PersonalizeActivity
                            intent = new Intent(LoginActivity.this, PersonalizeActivity.class);
                        }
                        // Truyền token nếu cần
                        intent.putExtra(TOKEN_KEY, token);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Không thể kiểm tra trạng thái đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi khi kiểm tra trạng thái đăng nhập: " + token, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Displays a toast message with the specified duration.
    private void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }
}