package com.ejn.cmov.acmecafe.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

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

    public String getStoredUserID(Context appContext) {
        String storedUserID = ((Result.Success<String>) dataSource.getUserID(appContext)).getData();

        if (this.userID == null)
            this.userID = storedUserID;

        return storedUserID;
    }

    public void storeUserID(Context appContext, String userID) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(
                appContext.getResources().getString(R.string.user_id_preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(appContext.getResources().getString(R.string.user_id), userID);
        editor.apply(); //Async

        Log.i("USER ID STORED", userID);

        this.userID = userID;
    }
}
