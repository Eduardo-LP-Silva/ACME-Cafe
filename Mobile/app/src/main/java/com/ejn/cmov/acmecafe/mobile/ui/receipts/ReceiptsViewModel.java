package com.ejn.cmov.acmecafe.mobile.ui.receipts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;

import java.lang.reflect.Array;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReceiptsViewModel extends ViewModel {
    private final RemoteDataRepository remoteDataRepository;
    private final LocalDataRepository localDataRepository;
    private final MutableLiveData<ReceiptModel[]> receipts;

    public ReceiptsViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
        this.receipts = new MutableLiveData<>();
    }

    public void loadRemoteReceipts(final Context appContext, String userID) {
        remoteDataRepository.getReceipts(userID, new Callback<ReceiptModel[]>() {
            @Override
            public void onComplete(Result<ReceiptModel[]> result) {
                if (result instanceof Result.Success) {
                    ReceiptModel[] newReceipts = ((Result.Success<ReceiptModel[]>) result).getData();
                    ReceiptModel[] oldReceipts = receipts.getValue();
                    ReceiptModel[] updatedReceipts;

                    if (oldReceipts != null) {
                        updatedReceipts = new ReceiptModel[newReceipts.length + oldReceipts.length];

                        for (int i = 0; i < updatedReceipts.length; i++)
                            if (i < oldReceipts.length)
                                updatedReceipts[i] = oldReceipts[i];
                            else
                                updatedReceipts[i] = newReceipts[i - oldReceipts.length];

                        receipts.postValue(updatedReceipts);
                    }
                    else {
                        updatedReceipts = newReceipts;
                    }

                    localDataRepository.storeReceipts(appContext, updatedReceipts);
                }
                else {
                    Log.e("RVM \\ GET REM. RECEIPTS", "COULDN'T FETCH RECEIPTS");
                    Toast.makeText(appContext, appContext.getResources().getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void loadLocalReceipts(final Context appContext) {
        localDataRepository.getStoredReceipts(appContext, new Callback<ReceiptModel[]>() {
            @Override
            public void onComplete(Result<ReceiptModel[]> result) {
                ReceiptModel[] localReceipts;

                if (result instanceof Result.Success) {
                    localReceipts = ((Result.Success<ReceiptModel[]>) result).getData();
                }
                else {
                    localReceipts = ((Result.Error<ReceiptModel[]>) result).getError();
                }

                receipts.postValue(localReceipts);
            }
        });
    }

    public MutableLiveData<ReceiptModel[]> getReceipts() {
        return receipts;
    }
}