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
    private LoggedInUser user = null;

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

    public void getReceipts(final String userID, final Callback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.getReceipts(userID);
                ReceiptModel[] receipts;

                if (res instanceof Result.Success) {
                    try {
                        JSONArray remoteReceipts = new JSONArray(((Result.Success<String>) res).getData());
                        receipts = new ReceiptModel[remoteReceipts.length()];

                        for (int i = 0; i < remoteReceipts.length(); i++) {
                            JSONObject receipt = remoteReceipts.getJSONObject(i);

                            JSONArray remoteItems = receipt.getJSONArray("items");
                            ItemModel[] items = new ItemModel[remoteItems.length()];

                            for (int j = 0; j < remoteItems.length(); j++) {
                                JSONObject remoteItem = remoteItems.getJSONObject(j);
                                JSONObject remoteItemDetails = remoteItem.getJSONObject("itemId");

                                items[j] = new ItemModel(remoteItemDetails.getString("_id"), remoteItemDetails.getString("name"),
                                        remoteItemDetails.getString("price"), remoteItemDetails.getString("icon"),
                                        remoteItemDetails.getString("updatedAt"), remoteItem.getString("quantity"));
                            }

                            //TODO Parse Vouchers
                            String date = receipt.getString("createdAt");
                            date = date.substring(0, 16);
                            date = date.replace('T', ' ');
                            receipts[i] = new ReceiptModel(items, date, receipt.getString("totalPrice"),
                                    false, 0);
                        }

                        Log.i("RDR \\ GET RECEIPTS", String.format("%d fetched", receipts.length));
                        callback.onComplete(new Result.Success<>(receipts));
                    }
                    catch (JSONException e) {
                        Log.e("RDR \\ GET RECEIPTS", e.toString());
                        receipts = new ReceiptModel[0];
                        callback.onComplete(new Result.Error<>(receipts));
                    }
                }
                else {
                    Log.e("RDR \\ GET RECEIPTS", ((Result.Error<String>) res).getError());
                    receipts = new ReceiptModel[0];
                    callback.onComplete(new Result.Error<>(receipts));
                }
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

    public void getCostumers(final Callback<String[]> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.getCostumers();
                String[] costumers;

                if (res instanceof  Result.Success) {
                    try {
                        JSONArray jsonArray = new JSONArray(((Result.Success<String>) res).getData());
                        costumers = new String[jsonArray.length()];

                        for (int i = 0; i < costumers.length; i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            costumers[i] = item.toString();
                        }

                        Log.i("RDR \\ GET COSTUMERS", String.format("%d fetched", costumers.length));
                        callback.onComplete(new Result.Success<>(costumers));
                    }
                    catch (JSONException e) {
                        Log.e("RDR \\ GET ITEMS", e.toString());
                        costumers = new String[0];
                        callback.onComplete(new Result.Error<>(costumers));
                    }
                }
                else {
                    Log.e("RDR \\ GET ITEMS", ((Result.Error<String>) res).getError());
                    costumers = new String[0];
                    callback.onComplete(new Result.Error<>(costumers));
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

    public void createOrder(String costumerId, String[][] items, final Callback<String> callback) {
        try {
            final JSONObject json = new JSONObject();
            json.put("costumerId", costumerId);
            ArrayList<JSONObject> itemsArrayObject = new ArrayList<JSONObject>();


            for (int i = 0; i < items.length; i++) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("itemId", items[i][0]);
                itemObject.put("quantity", items[i][1]);

                itemsArrayObject.add(itemObject);
            }
            json.put("items", itemsArrayObject.toArray());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Result<String> res = dataSource.createOrder(json);
                    callback.onComplete(res);
                }
            });
        }
        catch(JSONException e) {
            Log.e("REGISTER JSON EXCEPTION", e.toString());
        }
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