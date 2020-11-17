package com.ejn.cmov.acmecafe.terminal;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class RemoteDataRepository {
    private static volatile RemoteDataRepository instance;
    private static RemoteDataSource dataSource;
    private final Executor executor;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private RemoteDataRepository(Executor executor) {
        this.executor = executor;
        this.dataSource = RemoteDataSource.getInstance();
    }

    private RemoteDataRepository(Executor executor, String host, int port) {
        this.executor = executor;
        RemoteDataSource.host = host;
        RemoteDataSource.port = port;
        this.dataSource = RemoteDataSource.getInstance();
    }

    public static RemoteDataRepository getInstance(Executor executor) {
        if (instance == null)
            instance = new RemoteDataRepository(executor);

        return instance;
    }

    public static RemoteDataRepository getInstance(Executor executor, String host, int port) {
        if (instance == null)
            instance = new RemoteDataRepository(executor, host, port);

        return instance;
    }

    public void createOrder(final JSONObject json, final Callback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.createOrder(json);
                callback.onComplete(res);
            }
        });
    }

    public void getItems(final Callback<ItemModel[]> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.getItems();
                ItemModel[] items;

                if (res instanceof  Result.Success) {
                    try {
                        JSONArray jsonArray = new JSONArray(((Result.Success<String>) res).getData());
                        items = new ItemModel[jsonArray.length()];

                        for (int i = 0; i < items.length; i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            items[i] = new ItemModel(item.getString("_id"), item.getString("name"), item.getString("price"),
                                    item.getString("icon"), item.getString("updatedAt"), null);
                        }

                        Log.i("RDR \\ GET ITEMS", String.format("%d fetched", items.length));
                        callback.onComplete(new Result.Success<>(items));
                    }
                    catch (JSONException e) {
                        Log.e("RDR \\ GET ITEMS", e.toString());
                        items = new ItemModel[0];
                        callback.onComplete(new Result.Error<>(items));
                    }
                }
                else {
                    Log.e("RDR \\ GET ITEMS", ((Result.Error<String>) res).getError());
                    items = new ItemModel[0];
                    callback.onComplete(new Result.Error<>(items));
                }
            }
        });
    }

}