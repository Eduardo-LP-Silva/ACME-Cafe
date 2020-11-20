package com.ejn.cmov.acmecafe.mobile.ui.register;

import android.content.Context;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<String> registerResult = new MutableLiveData<>();
    private RemoteDataRepository remoteDataRepository;
    private LocalDataRepository localDataRepository;

    public RegisterViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
    }

    public void register(final Context appContext, String name, String nif, String cardNo, String expirationDate, String cvv,
                         final String username, final String pw) {
        registerResult.setValue(null);
        remoteDataRepository.register(appContext, name, nif, cardNo, expirationDate, cvv, new Callback<String>() {
            @Override
            public void onComplete(Result<String> result) {
                Log.i("REGISTRATION RESPONSE", result.toString());

                if (result instanceof Result.Success) {
                    try {
                        JSONObject resJson = new JSONObject(((Result.Success<String>) result).getData());
                        String userID = resJson.getString("customerId");
                        localDataRepository.storeUserCredentials(appContext, userID, username, pw);
                        registerResult.postValue(userID);
                    }
                    catch (JSONException e) {
                        Log.e("REGISTRATION RESPONSE", e.toString());
                    }
                }
                else {
                    registerResult.postValue("error");
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

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<String> getRegisterResult() {
        return registerResult;
    }

    private boolean isTextValid(String text, int minLength) {
        return text != null && text.length() >= minLength;
    }

    private boolean isNumberValid(String number, int length) {
        return number != null && number.length() == length;
    }

}
