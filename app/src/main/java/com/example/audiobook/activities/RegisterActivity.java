package com.example.audiobook.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.audiobook.R;
import com.example.audiobook.viewmodel.RegisterViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.example.audiobook.response.MessageKey;


public class RegisterActivity extends AppCompatActivity {
    private EditText editTextDateOfBirth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button cancelButton;
    private Button registerButton;
    private final Calendar myCalendar = Calendar.getInstance();
    private RegisterViewModel registerViewModel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        editTextDateOfBirth = findViewById(R.id.date_of_birth);
        cancelButton = findViewById(R.id.cancel);
        registerButton = findViewById(R.id.register_button);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(RegisterActivity.this, // Context
                        dateSetListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        editTextDateOfBirth.setFocusable(false);
        editTextDateOfBirth.setClickable(true);
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString(); // No trim for password usually
            String dobString = editTextDateOfBirth.getText().toString();
            if (email.isEmpty() || password.isEmpty() || dobString.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            Date dateOfBirth = null;
            try {
                dateOfBirth = sdf.parse(dobString);
            } catch (Exception e) {
                Log.e("RegisterActivity", "Date parsing error", e);
                Toast.makeText(this, "Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dateOfBirth != null) {
                Log.d("RegisterActivity", "Calling registerUser with: " + email + ", [PROTECTED PASSWORD], " + dateOfBirth.toString());
                registerViewModel.registerUser(email, password, dobString);
            }
        });
        registerViewModel.toast.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    if(message.equals(MessageKey.REGISTER_USER_SUCCESSFULLY)){
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }
}