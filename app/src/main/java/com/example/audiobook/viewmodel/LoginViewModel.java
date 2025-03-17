package com.example.audiobook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.audiobook.helper.SessionManager;
import com.example.audiobook.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> loginResult;
    private UserRepository userRepository;

    public LoginViewModel(SessionManager sessionManager) {
        userRepository = new UserRepository(sessionManager);
        loginResult = new MutableLiveData<>();
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        loginResult = userRepository.login(username, password);
    }

    public boolean isLoggedIn() {
        return userRepository.isLoggedIn();
    }
}