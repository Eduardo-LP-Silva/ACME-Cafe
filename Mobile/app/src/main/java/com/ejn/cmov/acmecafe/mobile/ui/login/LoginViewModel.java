package com.ejn.cmov.acmecafe.mobile.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private final String userName;
    private final String userPW;

    public LoginViewModel(String userName, String userPW) {
        this.userName = userName;
        this.userPW = userPW;
    }

    LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        loginResult.setValue(username.equals(this.userName) && password.equals(this.userPW));
    }
}