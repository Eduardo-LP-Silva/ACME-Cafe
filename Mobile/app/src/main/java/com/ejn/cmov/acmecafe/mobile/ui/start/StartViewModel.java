package com.ejn.cmov.acmecafe.mobile.ui.start;

import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartViewModel extends ViewModel {
    private LocalDataRepository localDataRepository;

    public StartViewModel(LocalDataRepository localDataRepository) {
        this.localDataRepository = localDataRepository;
    }

    public LocalDataRepository getLocalDataRepository() {
        return localDataRepository;
    }
}
