package com.example.audiobook.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.audiobook.fragments.OnBoardingItemFragment;

public class OnBoardingAdapter extends FragmentStateAdapter {

    public OnBoardingAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment cho mỗi item
        switch (position) {
            case 0:
                return OnBoardingItemFragment.newInstance("Item 1", "hello", "on_boarding_1");
            case 1:
                return OnBoardingItemFragment.newInstance("Item 2", "hello2", "on_boarding_2");
            case 2:
                return OnBoardingItemFragment.newInstance("Item 3", "hello3", "on_boarding_3");
            default:
                throw new IllegalStateException("Invalid position");
        }
    }
}