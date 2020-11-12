package com.ejn.cmov.acmecafe.mobile.ui.order;

import android.content.Context;
import android.telecom.Call;

import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.local.LocalDataRepository;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;
import com.ejn.cmov.acmecafe.mobile.data.remote.RemoteDataRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    private final RemoteDataRepository remoteDataRepository;
    private final LocalDataRepository localDataRepository;
    private final MutableLiveData<Hashtable<Integer, ArrayList<VoucherModel>>> vouchers;
    private MutableLiveData<Boolean> dataLoaded;

    public OrderViewModel(RemoteDataRepository remoteDataRepository, LocalDataRepository localDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
        this.localDataRepository = localDataRepository;
        this.vouchers = new MutableLiveData<>();
        this.dataLoaded = new MutableLiveData<>();
    }

    public void sendOrder(JSONObject payload) {
        remoteDataRepository.sendOrder(payload);
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
                dataLoaded.postValue(true);
            }
        });
    }

    public void getNewVouchers(final Context appContext, String userID) {
        remoteDataRepository.getVouchers(userID, new Callback<VoucherModel[]>() {
            @Override
            public void onComplete(Result<VoucherModel[]> result) {
                VoucherModel[] remoteVouchers;
                ArrayList<VoucherModel> coffeeVouchers = new ArrayList<>();
                ArrayList<VoucherModel> discountVouchers = new ArrayList<>();

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

                Hashtable<Integer, ArrayList<VoucherModel>> newVoucherTable = new Hashtable<>();
                newVoucherTable.put(0, coffeeVouchers);
                newVoucherTable.put(1, discountVouchers);

                vouchers.postValue(newVoucherTable);
                saveVouchers(appContext, newVoucherTable);
            }
        });
    }

    public void saveVouchers(Context appContext, Hashtable<Integer, ArrayList<VoucherModel>> voucherTable) {
        localDataRepository.storeVouchers(appContext, voucherTable);
    }

    public MutableLiveData<Hashtable<Integer, ArrayList<VoucherModel>>> getVouchers() {
        return vouchers;
    }

    public MutableLiveData<Boolean> getDataLoaded() {
        return dataLoaded;
    }
}