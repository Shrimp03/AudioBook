package com.example.audiobook.repository;


import androidx.lifecycle.MutableLiveData;
import com.example.audiobook.models.Audiobook;
import java.util.ArrayList;
import java.util.List;

public class AudiobookRepository {
    public MutableLiveData<List<Audiobook>> getAudiobooks() {
        MutableLiveData<List<Audiobook>> audiobookData = new MutableLiveData<>();
        // Giả lập dữ liệu (thay bằng API thực tế)
        List<Audiobook> audiobooks = new ArrayList<>();
        audiobooks.add(new Audiobook("Book 1", "android.resource://com.example.audiobookapp/raw/sample_audio"));
        audiobooks.add(new Audiobook("Book 2", "android.resource://com.example.audiobookapp/raw/sample_audio"));
        audiobookData.setValue(audiobooks);
        return audiobookData;
    }
}
