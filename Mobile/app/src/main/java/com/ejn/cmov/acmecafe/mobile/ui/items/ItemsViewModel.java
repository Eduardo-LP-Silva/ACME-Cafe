package com.ejn.cmov.acmecafe.mobile.ui.items;

import android.content.Context;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemsViewModel extends ViewModel {
    private final RemoteDataRepository remoteDataRepository;
    private final LocalDataRepository localDataRepository;
    private final MutableLiveData<ItemModel[]> items;

    public ItemsViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
        this.items = new MutableLiveData<>();
    }

    public void populateItems(final Context appContext) {
        localDataRepository.getStoredItems(appContext, new Callback<ItemModel[]>() {
            @Override
            public void onComplete(Result<ItemModel[]> localResult) {
                final ItemModel[] localItems;

                if (localResult instanceof Result.Success)
                    localItems = ((Result.Success<ItemModel[]>) localResult).getData();
                else
                    localItems = ((Result.Error<ItemModel[]>) localResult).getError();

                Log.i("IVM \\ Got Local Items", String.format("Items Length: %d", localItems.length));

                remoteDataRepository.getItems(new Callback<ItemModel[]>() {
                    @Override
                    public void onComplete(Result<ItemModel[]> remoteResult) {
                        if (remoteResult instanceof Result.Success) {
                            ItemModel[] remoteItems = ((Result.Success<ItemModel[]>) remoteResult).getData();

                            Log.i("IVM \\ Got Remote Items", String.format("Items Length: %d", remoteItems.length));

                            for (int i = 0; i < remoteItems.length; i++)
                                if (i >= localItems.length || !localItems[i].equals(remoteItems[i])) {
                                    localDataRepository.storeItems(appContext, remoteItems);
                                    items.postValue(remoteItems);
                                    Log.i("IVM", "UPDATED LOCAL ITEMS");
                                    return;
                                }
                        }

                        items.postValue(localItems);
                    }
                });
            }
        });
    }

    public MutableLiveData<ItemModel[]> getItems() {
        return items;
    }
}