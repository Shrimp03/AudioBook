package com.example.audiobook.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.audiobook.R;
import com.example.audiobook.activities.LoginActivity;
import com.example.audiobook.helper.SessionManager;
import com.example.audiobook.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;

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
    private LoginViewModel loginViewModel;
    private SessionManager sessionManager;
    private TextView tvViewProfile;

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
        
        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        
        // Initialize logout button
        btnLogout = view.findViewById(R.id.btn_logout);
        
        // Initialize view profile TextView
        tvViewProfile = view.findViewById(R.id.tv_view_profile);
        
        // Set click listener for logout button
        btnLogout.setOnClickListener(v -> handleLogout());
        
        // Set click listener for view profile TextView
        tvViewProfile.setOnClickListener(v -> navigateToProfileFragment());
        
        return view;
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
}