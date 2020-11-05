package com.ejn.cmov.acmecafe.mobile.ui;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.ejn.cmov.acmecafe.mobile.data.RemoteDataSource;
import com.ejn.cmov.acmecafe.mobile.data.DataRepository;
import com.ejn.cmov.acmecafe.mobile.data.ThreadExecutor;
import com.ejn.cmov.acmecafe.mobile.ui.login.LoginViewModel;
import com.ejn.cmov.acmecafe.mobile.ui.register.RegisterViewModel;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class) || modelClass.isAssignableFrom(RegisterViewModel.class)) {
            try {
                Constructor<?> cons = modelClass.getConstructor(DataRepository.class);
                return (T) cons.newInstance(DataRepository.getInstance(new RemoteDataSource(), new ThreadExecutor()));
            }
            catch(Exception e) {
                Log.e("View Model Factory", e.toString());
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}