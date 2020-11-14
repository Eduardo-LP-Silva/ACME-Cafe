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
}