package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.content.Context;

import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;

import androidx.lifecycle.ViewModel;

public class ItemsViewModel extends ViewModel {
    private RemoteDataRepository remoteDataRepository;
    private LocalDataRepository localDataRepository;

    public ItemsViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
    }

    public void populateItems(Context appContext, Callback<String> callback) {
        localDataRepository.getStoredItems(appContext, new Callback<ItemModel[]>() {
            @Override
            public void onComplete(Result<ItemModel[]> result) {
                if (result instanceof Result.Success) {
                    //TODO
                }
                else {
                    //TODO
                }
            }
        });
    }
}