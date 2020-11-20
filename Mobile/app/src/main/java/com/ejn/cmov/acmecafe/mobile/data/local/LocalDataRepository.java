package com.ejn.cmov.acmecafe.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executor;

public class LocalDataRepository {
    private static volatile LocalDataRepository instance;
    private LocalDataSource dataSource;
    private final Executor executor;
    private String userID;

    // private constructor : singleton access
    private LocalDataRepository(LocalDataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public static LocalDataRepository getInstance(LocalDataSource dataSource, Executor executor) {
        if (instance == null)
            instance = new LocalDataRepository(dataSource, executor);

        return instance;
    }

    public void storeVouchers(final Context appContext, final Hashtable<Integer, ArrayList<VoucherModel>> vouchers) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dataSource.storeVouchers(appContext, vouchers);
                Log.i("LDR \\ Stored Vouchers", String.format("Coffee Vouchers: %d | Discount Vouchers: %d", vouchers.get(0).size(),
                        vouchers.get(1).size()));
            }
        });
    }

    public void getStoredVouchers(final Context appContext, final Callback<Hashtable<Integer, ArrayList<VoucherModel>>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<Hashtable<Integer, ArrayList<VoucherModel>>> res = dataSource.getVouchers(appContext);
                callback.onComplete(res);
            }
        });
    }

    public void storeReceipts(final Context appContext, final ReceiptModel[] receipts) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dataSource.storeReceipts(appContext, receipts);
                Log.i("LDR \\ Stored Receipts", String.format("Items Length: %d", receipts.length));
            }
        });
    }

    public void getStoredReceipts(final Context appContext, final Callback<ReceiptModel[]> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<ReceiptModel[]> res = dataSource.getReceipts(appContext);
                callback.onComplete(res);
            }
        });
    }

    public void storeItems(final Context appContext, final ItemModel[] items) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                dataSource.storeItems(appContext, items);
                Log.i("LDR \\ Stored Items", String.format("Items Length: %d", items.length));
            }
        });
    }

    public void getStoredItems(final Context appContext, final Callback<ItemModel[]> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<ItemModel[]> result = dataSource.getItems(appContext);
                callback.onComplete(result);
            }
        });
    }

    public String[] getStoredUserCredentials(Context appContext) {
        String[] storedUserCredentials = ((Result.Success<String[]>) dataSource.getUserCredentials(appContext)).getData();

        if (this.userID == null && !storedUserCredentials[0].equals(appContext.getString(R.string.empty)))
            this.userID = storedUserCredentials[0];

        return storedUserCredentials;
    }

    public void storeUserCredentials(Context appContext, String userID, String username, String password) {
        this.userID = userID;

        SharedPreferences sharedPreferences = appContext.getSharedPreferences(
                appContext.getResources().getString(R.string.user_preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(appContext.getString(R.string.user_id), userID);
        editor.putString(appContext.getString(R.string.user_name), username);
        editor.putString(appContext.getString(R.string.user_password), password);
        editor.apply(); //Async

        Log.i("USER CREDENTIALS STORED", userID);
    }

    public String getUserID() {
        return userID;
    }
}
