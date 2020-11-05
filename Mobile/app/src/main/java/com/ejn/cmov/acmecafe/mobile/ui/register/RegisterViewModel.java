package com.ejn.cmov.acmecafe.mobile.ui.register;

import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.DataRepository;
import com.ejn.cmov.acmecafe.mobile.data.RemoteCallback;
import com.ejn.cmov.acmecafe.mobile.data.Result;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<Boolean> registerResult = new MutableLiveData<>();
    private DataRepository dataRepository;

    public RegisterViewModel(DataRepository remoteDataRepository) {
        this.dataRepository = remoteDataRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }
    LiveData<Boolean> getRegisterResult() {
        return registerResult;
    }

    public void register(String name, String nif, String cardNo, String expirationDate, String cvv, String username, String pw) {
        registerResult.setValue(null);
        //TODO Generate public key - certificate
        dataRepository.register(name, nif, cardNo, expirationDate, cvv, new RemoteCallback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                Log.i("REGISTRATION", "CALLBACK");
                if (result instanceof Result.Success) {
                    registerResult.postValue(true);
                }
                else {
                    registerResult.postValue(false);
                }
            }
        });
    }

    public void registerDataChanged(String name, String username, String nif, String cardNo, String expirationDate, String cvv,
                                    String password, String confirmPassword) {
        if (!isTextValid(name, 0)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_name, null, null,
                    null, null, null, null, null));
        }
        else if (!isTextValid(username, 0)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_username, null,
                    null, null, null, null, null));
        }
        else if (!isNumberValid(nif, 9)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_nif,
                    null, null, null, null, null));
        }
        else if (!isNumberValid(cardNo, 16)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_card_no,
                    null, null, null, null));
        }
        else if (!isNumberValid(expirationDate, 4)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null,
                    R.string.invalid_expiration_date, null, null, null));
        }
        else if (!isNumberValid(cvv, 3)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null,
                    null, R.string.invalid_cvv, null, null));
        }
        else if (!isTextValid(password, 5)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null,
                    null, null, R.string.invalid_password, null));
        }
        else if (!confirmPassword.equals(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, null,
                    null, null, null, R.string.invalid_confirm_password));
        }
        else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isTextValid(String text, int minLength) {
        return text != null && text.length() >= minLength;
    }

    private boolean isNumberValid(String number, int length) {
        return number != null && number.length() == length;
    }

}
