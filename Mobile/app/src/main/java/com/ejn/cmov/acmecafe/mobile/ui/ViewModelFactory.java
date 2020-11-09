package com.ejn.cmov.acmecafe.mobile.ui;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataSource;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataSource;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.ThreadExecutor;
import com.ejn.cmov.acmecafe.mobile.ui.items.ItemsViewModel;
import com.ejn.cmov.acmecafe.mobile.ui.receipts.ReceiptsViewModel;
import com.ejn.cmov.acmecafe.mobile.ui.register.RegisterViewModel;
import com.ejn.cmov.acmecafe.mobile.ui.start.StartViewModel;

import java.lang.reflect.Constructor;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Classes with both remote and local repository access
        if (modelClass.isAssignableFrom(RegisterViewModel.class) || modelClass.isAssignableFrom(ItemsViewModel.class)
            || modelClass.isAssignableFrom(ReceiptsViewModel.class)) {
            try {
                Constructor<?> cons = modelClass.getConstructor(RemoteDataRepository.class, LocalDataRepository.class);
                return (T) cons.newInstance(RemoteDataRepository.getInstance(new RemoteDataSource(), new ThreadExecutor()),
                        LocalDataRepository.getInstance(new LocalDataSource(), new ThreadExecutor()));
            }
            catch(Exception e) {
                Log.e("View Model Factory", e.toString());
                return null;
            }
        }   //Classes with only Local repository access
        else if (modelClass.isAssignableFrom(StartViewModel.class)) {
            try {
                Constructor<?> cons = modelClass.getConstructor(LocalDataRepository.class);
                return (T) cons.newInstance(LocalDataRepository.getInstance(new LocalDataSource(), new ThreadExecutor()));
            }
            catch(Exception e) {
                Log.e("View Model Factory", e.toString());
                return null;
            }
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}