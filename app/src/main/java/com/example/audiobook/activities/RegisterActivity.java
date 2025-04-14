package com.example.audiobook.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.audiobook.R;
import com.example.audiobook.dto.request.RegisterRequest;
import com.example.audiobook.dto.response.MessageKey;
import com.example.audiobook.viewmodel.RegisterViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText dateOfBirthEditText;
    private Button registerButton;
    private Button cancelButton;
    private RegisterViewModel registerViewModel;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViewModel();
        initializeViews();
        setupDatePicker();
        setupListeners();
        observeRegistrationResult();
    }

    // Initializes the RegisterViewModel.
    private void initializeViewModel() {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    // Initializes UI components from the layout.
    private void initializeViews() {
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        dateOfBirthEditText = findViewById(R.id.date_of_birth);
        registerButton = findViewById(R.id.register_button);
        cancelButton = findViewById(R.id.cancel);

        // Prevent manual text input for date field
        dateOfBirthEditText.setFocusable(false);
        dateOfBirthEditText.setClickable(true);
    }

    // Configures the date picker dialog for the date of birth field.
    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            dateOfBirthEditText.setText(dateFormatter.format(calendar.getTime()));
        };

        dateOfBirthEditText.setOnClickListener(v -> {
            new DatePickerDialog(this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    // Sets up click listeners for buttons and observes ViewModel events.
    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegistration());
        cancelButton.setOnClickListener(v -> finish());
    }

    // Validates input and initiates the registration process.
    private void handleRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString(); // Password typically not trimmed
        String dobString = dateOfBirthEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || dobString.isEmpty()) {
            showToast("Please fill in all fields.", Toast.LENGTH_SHORT);
            return;
        }

        try {
            dateFormatter.parse(dobString); // Validate date format
            Log.d(TAG, "Registering user with email: " + email + ", dob: " + dobString);

            RegisterRequest request = new RegisterRequest(email, password, dobString);
            registerViewModel.registerUser(request);
        } catch (ParseException e) {
            Log.e(TAG, "Invalid date format: " + dobString, e);
            showToast("Invalid date format (dd-MM-yyyy).", Toast.LENGTH_SHORT);
        }
    }

    // Observes registration results from the ViewModel.
    private void observeRegistrationResult() {
        registerViewModel.toastMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                showToast(message, Toast.LENGTH_LONG);
                if (MessageKey.REGISTER_USER_SUCCESSFULLY.equals(message)) {
                    navigateToLogin();
                }
            }
        });
    }

    // Navigates to the LoginActivity and closes this activity.
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Displays a toast message with the specified duration.
    // @param message  The message to display
    // @param duration The duration of the toast
    private void showToast(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }
}