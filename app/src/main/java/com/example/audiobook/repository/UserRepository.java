package com.example.audiobook.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.audiobook.helper.SessionManager;

public class UserRepository {
    private SessionManager sessionManager;

    public UserRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public MutableLiveData<Boolean> login(String username, String password) {
        MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
        if (username.equals("admin") && password.equals("12345")) {
            sessionManager.setLogin(true);
            loginResult.setValue(true);
        } else {
            loginResult.setValue(false);
        }
        return loginResult;
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
}