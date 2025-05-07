package com.example.audiobook.fragments;

import static com.example.audiobook.viewmodel.LoginViewModel.TOKEN_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.audiobook.R;
import com.example.audiobook.activities.LoginActivity;
import com.example.audiobook.helper.SessionManager;
import com.example.audiobook.viewmodel.LoginViewModel;
import com.example.audiobook.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;

import utils.Constant;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    private MaterialButton btnLogout;
    private ImageView ivProfilePhoto;
    private LoginViewModel loginViewModel;
    private SessionManager sessionManager;
    private TextView tvViewProfile;
    private Toolbar toolbar;
    private ProfileViewModel profileViewModel;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        profileViewModel.getImageUrl().observe(this, imageUrl -> {
            if (imageUrl != null) {
                updateProfilePhoto(imageUrl);
            }else{
                updateProfilePhoto(Constant.DEFAULT_IMG_URL);
            }
        });
        
        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());
        // Fetch user information from ViewModel
        String userId = getUserId();
        profileViewModel.fetchUserInformation(userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        
        // Initialize toolbar
        toolbar = view.findViewById(R.id.toolbar);
        ivProfilePhoto = view.findViewById(R.id.profile_image);
        
        // Initialize logout button
        btnLogout = view.findViewById(R.id.btn_logout);
        
        // Initialize view profile TextView
        tvViewProfile = view.findViewById(R.id.tv_view_profile);
        
        // Set click listener for logout button
        btnLogout.setOnClickListener(v -> handleLogout());
        
        // Set click listener for view profile TextView
        tvViewProfile.setOnClickListener(v -> navigateToProfileFragment());
        
        // Set click listener for toolbar navigation icon (back button)
        toolbar.setNavigationOnClickListener(v -> navigateToHomeFragment());
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Assuming imageUrl is retrieved from somewhere, e.g., ViewModel or arguments
        String imageUrl = profileViewModel.getImageUrl().getValue(); // Replace with actual image URL source
        updateProfilePhoto(imageUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // Reload the profile photo when the fragment resumes
        String imageUrl = profileViewModel.getImageUrl().getValue(); // Replace with actual image URL source
        updateProfilePhoto(imageUrl);
    }
    
    /**
     * Handles the logout process by clearing authentication data and navigating to login screen
     */
    private void handleLogout() {
        // Clear authentication token
        loginViewModel.clearToken();
        
        // Update login status in SessionManager
        sessionManager.setLogin(false);
        
        // Navigate to login screen
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        // Clear back stack so user can't navigate back to the app after logout
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        // Close the current activity
        requireActivity().finish();
    }
    
    /**
     * Navigates to the ProfileFragment
     */
    private void navigateToProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    
    /**
     * Navigates back to the HomeFragment
     */
    private void navigateToHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.commit();
    }
    private void updateProfilePhoto(String imageUrl) {
        Glide.with(requireContext())
                .load(imageUrl)
//             .placeholder(R.drawable.placeholder) // Optional placeholder
//             .error(R.drawable.error) // Optional error drawable
                .into(ivProfilePhoto);
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
}