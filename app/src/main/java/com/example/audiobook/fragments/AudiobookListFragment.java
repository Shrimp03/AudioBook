package com.example.audiobook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audiobook.R;
import com.example.audiobook.adapters.AudiobookAdapter;
import com.example.audiobook.models.Audiobook;
import com.example.audiobook.viewmodel.AudiobookViewModel;

import java.util.ArrayList;

public class AudiobookListFragment extends Fragment {
//    private RecyclerView recyclerView;
//    private AudiobookAdapter adapter;
//    private AudiobookViewModel viewModel;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_audiobook_list, container, false);
//
//        recyclerView = view.findViewById(R.id.recycler_view_audiobooks);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new AudiobookAdapter(new ArrayList<>(), audiobook -> {
//            PlayerFragment playerFragment = new PlayerFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("filePath", audiobook.getFilePath());
//            bundle.putString("title", audiobook.getTitle());
//            playerFragment.setArguments(bundle);
//            getActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, playerFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });
//        recyclerView.setAdapter(adapter);
//
//        viewModel = new ViewModelProvider(this).get(AudiobookViewModel.class);
//        viewModel.getAudiobookList().observe(getViewLifecycleOwner(), audiobooks -> {
//            if (audiobooks != null) {
//                adapter.updateList(audiobooks);
//            } else {
//                Toast.makeText(getContext(), "Failed to load audiobooks", Toast.LENGTH_SHORT).show();
//            }
//        });
//        viewModel.fetchAudiobooks();
//
//        return view;
//    }
}
