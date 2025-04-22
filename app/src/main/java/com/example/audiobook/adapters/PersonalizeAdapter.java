package com.example.audiobook.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.audiobook.fragments.PersonalizeItemFragment;
import com.example.audiobook.dto.response.CategoryResponse;
import java.util.List;

public class PersonalizeAdapter extends FragmentStateAdapter {
    private List<CategoryResponse> categories;

    public PersonalizeAdapter(FragmentActivity fragmentActivity, List<CategoryResponse> categories) {
        super(fragmentActivity);
        this.categories = categories;
    }

    @Override
    public int getItemCount() {
        return 3; // 3 trang
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PersonalizeItemFragment.newInstance(
                        PersonalizeItemFragment.TYPE_INTRO,
                        "Welcome !",
                        "Find what you are looking for",
                        "By personalize your account, we can help you to find what you like.",
                        categories
                );
            case 1:
                return PersonalizeItemFragment.newInstance(
                        PersonalizeItemFragment.TYPE_CATEGORIES,
                        "",  // Không cần subtitle cho trang Categories
                        "Chọn danh mục yêu thích",
                        "",
                        categories
                );
            case 2:
                return PersonalizeItemFragment.newInstance(
                        PersonalizeItemFragment.TYPE_FINISH,
                        "Congratulations !",
                        "Hoàn tất!",
                        "Bạn đã chọn xong các danh mục yêu thích. Bắt đầu trải nghiệm ngay nào!",
                        categories
                );
            default:
                throw new IllegalStateException("Invalid position");
        }
    }
}