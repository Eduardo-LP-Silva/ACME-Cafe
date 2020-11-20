package com.ejn.cmov.acmecafe.mobile.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ejn.cmov.acmecafe.mobile.R;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private final String userName;
    private final String userPW;

    public LoginViewModel(String userName, String userPW) {
        this.userName = userName;
        this.userPW = userPW;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        loginResult.setValue(username.equals(this.userName) && password.equals(this.userPW));
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        return !username.trim().isEmpty();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}