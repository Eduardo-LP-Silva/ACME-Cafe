package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.content.Context;
import android.telecom.Call;

import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;

import java.util.ArrayList;
import java.util.Hashtable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    private final RemoteDataRepository remoteDataRepository;
    private final LocalDataRepository localDataRepository;
    private final MutableLiveData<Hashtable<Integer, ArrayList<VoucherModel>>> vouchers;

    public OrderViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
        this.vouchers = new MutableLiveData<>();
    }

    public void loadLocalVouchers(Context appContext) {
        localDataRepository.getStoredVouchers(appContext, new Callback<Hashtable<Integer, ArrayList<VoucherModel>>>() {
            @Override
            public void onComplete(Result<Hashtable<Integer, ArrayList<VoucherModel>>> result) {
                Hashtable<Integer, ArrayList<VoucherModel>> localTable;

                if(result instanceof Result.Success)
                    localTable = ((Result.Success<Hashtable<Integer, ArrayList<VoucherModel>>>) result).getData();
                else
                    localTable = ((Result.Error<Hashtable<Integer, ArrayList<VoucherModel>>>) result).getError();

                vouchers.postValue(localTable);
            }
        });
    }

    public void getNewVouchers(final Context appContext, String userID) {
        remoteDataRepository.getVouchers(userID, new Callback<VoucherModel[]>() {
            @Override
            public void onComplete(Result<VoucherModel[]> result) {
                VoucherModel[] remoteVouchers;
                Hashtable<Integer, ArrayList<VoucherModel>> table = vouchers.getValue();
                ArrayList<VoucherModel> coffeeVouchers = table.get(0);
                ArrayList<VoucherModel> discountVouchers = table.get(1);

                if (result instanceof Result.Success)
                    remoteVouchers = ((Result.Success<VoucherModel[]>) result).getData();
                else
                    remoteVouchers = ((Result.Error<VoucherModel[]>) result).getError();

                for (VoucherModel voucher: remoteVouchers) {
                    if (voucher.getType() == 0)
                        coffeeVouchers.add(voucher);
                    else if(voucher.getType() == 1)
                        discountVouchers.add(voucher);
                }

                Hashtable<Integer, ArrayList<VoucherModel>> newTable = new Hashtable<>();
                newTable.put(0, coffeeVouchers);
                newTable.put(1, discountVouchers);

                vouchers.postValue(newTable);
                localDataRepository.storeVouchers(appContext, table);
            }
        });
    }

    public MutableLiveData<Hashtable<Integer, ArrayList<VoucherModel>>> getVouchers() {
        return vouchers;
    }
}