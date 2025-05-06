package com.example.audiobook.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.audiobook.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView ivProfilePhoto, ivEditPhoto;
    private TextView tvDisplayName, tvUsername, tvEmail, tvPhone, tvBirthdate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt menu cho fragment này
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Toolbar: back → popBackStack
        toolbar = view.findViewById(R.id.toolbar_profile);
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
                textView.setPadding(16, 0, 45, 0);

                textView.setGravity(Gravity.CENTER);
                textView.setOnClickListener(v -> {
                    // Handle save action
                    // TODO: xử lý lưu profile
                });
                saveMenuItem.setActionView(textView);
            }
        }
        
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                // Xử lý Save
                return true;
            }
            return false;
        });

        // Bind các view
        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);
        ivEditPhoto    = view.findViewById(R.id.iv_edit_photo);
        tvDisplayName  = view.findViewById(R.id.tv_display_name);
        tvUsername     = view.findViewById(R.id.tv_username);
        tvEmail        = view.findViewById(R.id.tv_email);
        tvPhone        = view.findViewById(R.id.tv_phone);
        tvBirthdate    = view.findViewById(R.id.tv_birthdate);

        // Click vào icon edit avatar
        ivEditPhoto.setOnClickListener(v -> {
            // TODO: mở dialog/chọn ảnh mới
        });

        // TODO: nạp dữ liệu thực tế từ ViewModel hoặc arguments vào các TextView

        return view;
    }
}