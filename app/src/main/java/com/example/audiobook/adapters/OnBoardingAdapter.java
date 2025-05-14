package com.example.audiobook.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.audiobook.fragments.OnBoardingItemFragment;

public class OnBoardingAdapter extends FragmentStateAdapter {

    private static final int PAGE_COUNT = 3;
    private static final String[] TITLES = {"Discover the World of Audiobooks", "Flexible Listening Experience", "Personalized for You"};
    private static final String[] DESCRIPTIONS = {"Access thousands of high-quality audiobooks anytime, anywhere.", "Enjoy seamless listening across all your devices.", "Get content recommendations based on your listening habits."};
    private static final String[] IMAGE_RES_NAMES = {"on_boarding_1", "on_boarding_2", "on_boarding_3"};

    public OnBoardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position < 0 || position >= PAGE_COUNT) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return OnBoardingItemFragment.newInstance(
                TITLES[position],
                DESCRIPTIONS[position],
                IMAGE_RES_NAMES[position]
        );
    }
}