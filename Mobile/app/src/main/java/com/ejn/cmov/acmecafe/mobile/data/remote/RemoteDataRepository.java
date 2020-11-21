package com.ejn.cmov.acmecafe.mobile.data.remote;

import android.content.Context;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.data.Authentication;
import com.ejn.cmov.acmecafe.mobile.data.Callback;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;

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
    private final RemoteDataSource dataSource;
    private final Executor executor;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

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

    public void sendOrder(final JSONObject payload) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.sendOrder(payload);

                if (res instanceof Result.Success)
                    Log.i("RDR \\ SEND ORDER", ((Result.Success<String>) res).getData());
                else
                    Log.e("RDR \\ SEND ORDER", ((Result.Error<String>) res).getError());
            }
        });
    }

    public void getVouchers(final String userID, final Callback<VoucherModel[]> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<String> res = dataSource.getVouchers(userID);
                VoucherModel[] vouchers;

                if (res instanceof Result.Success) {
                    try {
                        JSONArray remoteVouchers = new JSONArray(((Result.Success<String>) res).getData());
                        vouchers = new VoucherModel[remoteVouchers.length()];

                        for (int i = 0; i < remoteVouchers.length(); i++) {
                            JSONObject voucher = remoteVouchers.getJSONObject(i);
                            vouchers[i] = new VoucherModel(voucher.getString("_id"), voucher.getInt("type"));
                        }

                        Log.i("RDR \\ GET VOUCHERS", String.format("%d fetched", vouchers.length));
                        callback.onComplete(new Result.Success<>(vouchers));
                    }
                    catch (JSONException e) {
                        Log.e("RDR \\ GET VOUCHERS", e.toString());
                        vouchers = new VoucherModel[0];
                        callback.onComplete(new Result.Error<>(vouchers));
                    }
                }
                else {
                    Log.e("RDR \\ GET VOUCHERS", ((Result.Error<String>) res).getError());
                    vouchers = new VoucherModel[0];
                    callback.onComplete(new Result.Error<>(vouchers));
                }
            }
        });
    }

    public void getReceipts(final String userID, final Callback<ReceiptModel[]> callback) {
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

                            JSONArray remoteVouchers = receipt.getJSONArray("vouchers");
                            VoucherModel discountVoucher = null;
                            ArrayList<VoucherModel> coffeeVouchers = new ArrayList<>();

                            for (int j = 0; j < remoteVouchers.length(); j++) {
                                JSONObject remoteVoucher = remoteVouchers.getJSONObject(j);

                                if (remoteVoucher.getInt("type") == 0)
                                    coffeeVouchers.add(new VoucherModel(remoteVoucher.getString("_id"), 0));
                                else
                                    discountVoucher = new VoucherModel(remoteVoucher.getString("_id"), 1);
                            }

                            String date = receipt.getString("createdAt");
                            date = date.substring(0, 16);
                            date = date.replace('T', ' ');
                            receipts[i] = new ReceiptModel(items, date, receipt.getString("totalPrice"),
                                    discountVoucher, coffeeVouchers);
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
                                    item.getString("icon"), item.getString("updatedAt"), item.getString("quantity"));
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

    public void register(final Context appContext, final String name, final String nif, final String cardNo, final String expirationDate, final String cvv,
                         final Callback<String> callback) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                final JSONObject json = new JSONObject();

                try {
                    json.put("name", name);
                    json.put("bankCardNumber", cardNo);
                    json.put("bankCardExpiry", expirationDate);
                    json.put("bankCardCVV", cvv);
                    json.put("nif", nif);
                    json.put("publicKey", Authentication.getCertificate(appContext));
                }
                catch(JSONException e) {
                    Log.e("REGISTER JSON EXCEPTION", e.toString());
                }

                Result<String> res = dataSource.register(json);
                callback.onComplete(res);
            }
        });
    }
}