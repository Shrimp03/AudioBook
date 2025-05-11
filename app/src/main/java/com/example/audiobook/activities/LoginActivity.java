package com.example.audiobook.activities;

import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.audiobook.R;
import com.example.audiobook.api.UserApi;
import com.example.audiobook.dto.request.FCMTokenRequest;
import com.example.audiobook.dto.request.LoginRequest;
import com.example.audiobook.dto.response.MessageKey;
import com.example.audiobook.dto.response.ResponseObject;
import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.repository.CategoryRepository;
import com.example.audiobook.repository.UserRepository;
import com.example.audiobook.viewmodel.LoginViewModel;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    // UI components
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    // ViewModel for handling login logic
    private LoginViewModel loginViewModel;
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeViewModel();
        setupListeners();
        observeLoginResult();
        userRepository = new UserRepository();
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
                        // Get FCM token and send to backend
                        getAndSendFCMToken(token);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Lỗi khi không có token: " + token, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getAndSendFCMToken(String authToken) {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String fcmToken = task.getResult();
                Log.d(TAG, "FCM Token: " + fcmToken);
                String userId = null;
                try{
                    com.auth0.android.jwt.JWT jwt = new com.auth0.android.jwt.JWT(authToken);
                    userId   = jwt.getClaim("sub").asString();

                }catch (com.auth0.android.jwt.DecodeException e){
                    e.printStackTrace();
                }

                // Send FCM token to backend
                FCMTokenRequest request = new FCMTokenRequest(userId, fcmToken);
                userRepository.updatedFcmToken(request).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "FCM token updated successfully");
                        } else {
                            Log.e(TAG, "Failed to update FCM token: " + response.code());
                        }
                        // Navigate to appropriate screen regardless of FCM token update result
                        navigateToMain(authToken);
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        Log.e(TAG, "Error updating FCM token", t);
                        // Navigate to appropriate screen even if FCM token update fails
                        navigateToMain(authToken);
                    }
                });
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