package com.example.audiobook.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.dto.response.UserResponse;
import com.example.audiobook.viewmodel.LoginViewModel;
import com.example.audiobook.viewmodel.ProfileViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import utils.Constant;
import utils.FormatDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private LoginViewModel loginViewModel;


    private Toolbar toolbar;
    private ImageView ivProfilePhoto;
    private EditText tvDisplayName, tvUsername, tvBirthdate;
    private TextView tvEmail, tvPhone;
    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Uri selectedImageUri;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private View rootView;
    private ScrollView scrollView;
    private ProfileViewModel profileViewModel;

    // Activity result launcher for image picking
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt menu cho fragment này
        setHasOptionsMenu(true);

        // Initialize ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Initialize date formatter
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();

        // Initialize the activity result launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(), selectedImageUri);
                        ivProfilePhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
        profileViewModel.getUsername().observe(this, username -> {
            if (username != null) {
                tvUsername.setText(username);
            }
        });

        profileViewModel.getDisplayName().observe(this, displayName -> {
            if (displayName != null) {
                tvDisplayName.setText(displayName);
            }
        });

        profileViewModel.getDateOfBirth().observe(this, dateOfBirth -> {
            if (dateOfBirth != null) {
                tvBirthdate.setText(FormatDate.formatDateOfBirth(dateOfBirth));
            }
        });
        profileViewModel.getImageUrl().observe(this, imageUrl -> {
            if (imageUrl != null) {
                updateProfilePhoto(imageUrl);
            }else{
                updateProfilePhoto(Constant.DEFAULT_IMG_URL);
            }
        });
        // Fetch user information from ViewModel
        String userId = getUserId();
        profileViewModel.fetchUserInformation(userId);

        // Observe ViewModel results
        profileViewModel.getUpdateResult().observe(this, userResponse -> {
            if (userResponse != null ) {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                // Update UI with new data if needed
                updateUIWithUserData(userResponse);
            }
        });

        profileViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Toolbar: back → popBackStack
        toolbar = rootView.findViewById(R.id.toolbar_profile);
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
        toolbar.inflateMenu(R.menu.menu_profile);

        // Style the Save menu item
        MenuItem saveMenuItem = toolbar.getMenu().findItem(R.id.action_save);
        if (saveMenuItem != null) {
            // Get the text view from the action view
            View actionView = saveMenuItem.getActionView();
            if (actionView == null) {
                // If no custom action view is set, create a TextView
                TextView textView = new TextView(requireContext());
                textView.setText(R.string.save);
                textView.setTextColor(getResources().getColor(R.color.primary_50));
                textView.setTextSize(18);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setPadding(16, 0, 55, 0);

                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(v -> {
                    // Handle save action
                    saveProfileChanges();
                });
                saveMenuItem.setActionView(textView);
            }
        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                // Xử lý Save
                saveProfileChanges();
                return true;
            }
            return false;
        });

        // Bind các view
        ivProfilePhoto = rootView.findViewById(R.id.iv_profile_photo);

        // Bind editable fields
        tvDisplayName = rootView.findViewById(R.id.tv_display_name);
        tvUsername = rootView.findViewById(R.id.tv_username);
        tvBirthdate = rootView.findViewById(R.id.tv_birthdate);

        // Bind non-editable fields
        tvEmail = rootView.findViewById(R.id.tv_email);
        tvPhone = rootView.findViewById(R.id.tv_phone);

        // Get the ScrollView to implement touch listener
        scrollView = rootView.findViewById(R.id.scroll_view_profile);

        // Setup touch listener to clear focus when tapping outside editable fields
        setupTouchListener();

        // Setup editable fields with long press listeners
        setupEditableFields();

        // Click vào icon edit avatar
        rootView.findViewById(R.id.iv_edit_photo).setOnClickListener(v -> {
            // Check for permissions first
            if (checkAndRequestPermissions()) {
                openImagePicker();
            }
        });

        // TODO: nạp dữ liệu thực tế từ ViewModel hoặc arguments vào các TextView

        return rootView;
    }

    /**
     * Setup touch listener to clear focus when tapping outside editable fields
     */
    private void setupTouchListener() {
        // Set touch listener on the root view to detect touches outside editable fields
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clearFocusIfOutsideEditableFields(event);
            }
            return false; // Allow the touch event to continue to other views
        });

        // Also set touch listener on ScrollView to handle touches in the scrollable area
        if (scrollView != null) {
            scrollView.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    clearFocusIfOutsideEditableFields(event);
                }
                return false; // Allow scrolling to work
            });
        }
    }

    /**
     * Setup editable fields with long press listeners
     */
    private void setupEditableFields() {
        // Setup long click listeners for editable fields
        View.OnLongClickListener longClickListener = v -> {
            EditText editText = (EditText) v;
            if (editText == tvBirthdate) {
                showDatePicker();
            } else {
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
            }
            return true;
        };

        // Set up each editable field
        tvDisplayName.setOnLongClickListener(longClickListener);
        tvUsername.setOnLongClickListener(longClickListener);
        tvBirthdate.setOnLongClickListener(longClickListener);

        // Prevent keyboard from showing up for date field
        tvBirthdate.setInputType(InputType.TYPE_NULL);
    }

    /**
     * Show date picker dialog
     */
    private void showDatePicker() {
        // Parse current date from tvBirthdate or use current date if empty/invalid
        Calendar currentDate = Calendar.getInstance();
        try {
            if (tvBirthdate.getText() != null && !tvBirthdate.getText().toString().isEmpty()) {
                currentDate.setTime(dateFormatter.parse(tvBirthdate.getText().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvBirthdate.setText(dateFormatter.format(calendar.getTime()));
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set min date (e.g., 100 years ago)
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    /**
     * Clear focus if the touch event is outside all editable fields
     */
    private void clearFocusIfOutsideEditableFields(MotionEvent event) {
        // Check if we have any focused view
        View focusedView = requireActivity().getCurrentFocus();
        if (focusedView instanceof EditText) {
            Rect outRect = new Rect();
            focusedView.getGlobalVisibleRect(outRect);

            // If the touch is outside the bounds of the focused EditText
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                // Clear focus and make non-focusable
                focusedView.clearFocus();
                ((EditText) focusedView).setFocusableInTouchMode(false);

                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) requireContext()
                        .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        }
    }

    /**
     * Save the changes made to the profile
     */
    private void saveProfileChanges() {
        String username = tvUsername.getText().toString().trim();
        String displayName = tvDisplayName.getText().toString().trim();
        String dateOfBirth = tvBirthdate.getText().toString().trim();

        // Validate input
        if (username.isEmpty() || displayName.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selected image URI to File if exists
        File avatarFile = null;
        if (selectedImageUri != null) {
            try {
                avatarFile = new File(getRealPathFromURI(selectedImageUri));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(LoginViewModel.PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if(token == null){
            return;
        }
        String userId = null;
        try{
            com.auth0.android.jwt.JWT jwt = new com.auth0.android.jwt.JWT(token);
            userId   = jwt.getClaim("sub").asString();

        }catch (com.auth0.android.jwt.DecodeException e){
            e.printStackTrace();
        }

        // Call ViewModel to update profile
        profileViewModel.updateUserProfile(userId,username, displayName, dateOfBirth, avatarFile);
    }

    private String getUserId(){
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(LoginViewModel.PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if(token == null){
            return null;
        }
        String userId = null;
        try{
            com.auth0.android.jwt.JWT jwt = new com.auth0.android.jwt.JWT(token);
            userId   = jwt.getClaim("sub").asString();
        }catch (com.auth0.android.jwt.DecodeException e){
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * Get real path from URI
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void updateUIWithUserData(UserResponse user) {
        if (user.getDisplayName() != null) {
            tvDisplayName.setText(user.getDisplayName());
        }
        if (user.getUsername()!= null) {
            tvUsername.setText(user.getUsername());
        }
        if (user.getDateOfBirth() != null) {
            tvBirthdate.setText(FormatDate.formatDateOfBirth(user.getDateOfBirth()));
        }
        if (user.getImageUrl() != null) {
            updateProfilePhoto(user.getImageUrl());
        }
        // Update other UI elements as needed
    }

    private void updateProfilePhoto(String imageUrl) {
        Glide.with(requireContext())
             .load(imageUrl)
//             .placeholder(R.drawable.placeholder) // Optional placeholder
//             .error(R.drawable.error) // Optional error drawable
             .into(ivProfilePhoto);
    }

    /**
     * Check if we have storage permissions and request if not granted
     */
    private boolean checkAndRequestPermissions() {
        // For Android 13+ (API 33+), we need to request READ_MEDIA_IMAGES permission
        // For older versions, we need READ_EXTERNAL_STORAGE
        String permission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * Open the image picker intent
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}