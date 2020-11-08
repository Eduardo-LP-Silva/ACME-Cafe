package com.ejn.cmov.acmecafe.mobile.data.remote;

import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class RemoteDataRepository {
    private static volatile RemoteDataRepository instance;
    private final RemoteDataSource dataSource;
    private final Executor executor;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private RemoteDataRepository(RemoteDataSource dataSource, Executor executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    public static RemoteDataRepository getInstance(RemoteDataSource dataSource, Executor executor) {
        if (instance == null)
            instance = new RemoteDataRepository(dataSource, executor);

        return instance;
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
                                    item.getString("icon"), item.getString("updatedAt"));
                        }

                        callback.onComplete(new Result.Success<>(items));
                    }
                    catch (JSONException e) {
                        Log.e("RDR \\ GET ITEMS", e.toString());
                        items = new ItemModel[0];
                        callback.onComplete(new Result.Error<>(items));
                    }
                }
                else {
                    items = new ItemModel[0];
                    callback.onComplete(new Result.Error<>(items));
                }
            }
        });
    }

    public void register(String name, String nif, String cardNo, String expirationDate, String cvv,
                         final Callback<String> callback) {
        try {
            final JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("bankCardNumber", cardNo);
            json.put("bankCardExpiry", expirationDate);
            json.put("bankCardCVV", cvv);
            json.put("nif", nif);
            json.put("publicKey", "f4r34frgw4g3");

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Result<String> res = dataSource.register(json);
                    callback.onComplete(res);
                }
            });
        }
        catch(JSONException e) {
            Log.e("REGISTER JSON EXCEPTION", e.toString());
        }
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public boolean isLoggedIn() {
        return user != null;
    }
}