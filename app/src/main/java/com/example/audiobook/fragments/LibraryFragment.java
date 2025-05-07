package com.example.audiobook.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiobook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LibraryFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvAudioList;
    private FloatingActionButton fabUploadAudio;
    // Placeholder cho adapter, thay bằng adapter thật của bạn
    // private AudioListAdapter audioListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        etSearch = view.findViewById(R.id.et_search);
        rvAudioList = view.findViewById(R.id.rv_audio_list);
        fabUploadAudio = view.findViewById(R.id.fab_upload_audio);

        // Cấu hình RecyclerView
        rvAudioList.setLayoutManager(new LinearLayoutManager(getContext()));
        // rvAudioList.setAdapter(audioListAdapter); // Thay bằng adapter của bạn

        // Xử lý tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAudioList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Chuyển sang UploadAudioFragment khi nhấn FAB
        fabUploadAudio.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new UploadAudioFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void filterAudioList(String query) {
        // Placeholder: Lọc danh sách audio
        // Ví dụ: audioListAdapter.filter(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAudioList();
    }

    private void refreshAudioList() {
        // Placeholder: Làm mới danh sách audio
        // Ví dụ: audioListAdapter.notifyDataSetChanged();
    }
}