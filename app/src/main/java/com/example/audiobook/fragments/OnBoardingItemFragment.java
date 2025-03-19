package com.example.audiobook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.audiobook.R;

public class OnBoardingItemFragment extends Fragment {

    public static OnBoardingItemFragment newInstance(String title, String desc, String src) {
        OnBoardingItemFragment fragment = new OnBoardingItemFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("DESC", desc);
        args.putString("SRC", src);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_item, container, false);
        TextView titleView = view.findViewById(R.id.on_title);
        ImageView imgView = view.findViewById(R.id.on_item_img);
        TextView descView = view.findViewById(R.id.on_desc);
        if (getArguments() != null) {
            titleView.setText(getArguments().getString("TITLE"));
            descView.setText(getArguments().getString("DESC"));
            String src = getArguments().getString("SRC");
            if(src.equals("on_boarding_1")){
                imgView.setImageResource(R.drawable.on_boarding1);
            }
            else if(src.equals("on_boarding_2")){
                imgView.setImageResource(R.drawable.on_boarding2);
            }
            else imgView.setImageResource(R.drawable.on_boarding3);
        }
        return view;
    }
}