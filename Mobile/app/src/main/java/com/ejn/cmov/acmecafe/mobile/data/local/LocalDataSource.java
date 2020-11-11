package com.ejn.cmov.acmecafe.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;
import com.ejn.cmov.acmecafe.mobile.data.model.ReceiptModel;
import com.ejn.cmov.acmecafe.mobile.data.model.VoucherModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

public class LocalDataSource {
    final String itemsFilePath = "menu_items.srl";
    final String receiptsFilePath = "receipts.srl";
    final String vouchersFilePath = "vouchers.srl";

    public void storeVouchers(Context appContext, Hashtable<Integer, ArrayList<VoucherModel>> vouchers) {
        try {
            ObjectOutput objOut = new ObjectOutputStream(new FileOutputStream(new File(appContext.getFilesDir(), "")
                    + File.separator + vouchersFilePath));
            objOut.writeObject(vouchers);
        }
        catch (Exception e) {
            Log.e("LDS \\ STORE VOUCHERS", e.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public Result<Hashtable<Integer, ArrayList<VoucherModel>>> getVouchers(Context appContext) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(new File(appContext.getFilesDir(), "")
                    + File.separator + vouchersFilePath)));
            Hashtable<Integer, ArrayList<VoucherModel>> vouchers = (Hashtable<Integer, ArrayList<VoucherModel>>) inputStream.readObject();
            inputStream.close();

            return new Result.Success<>(vouchers);
        }
        catch (Exception e) {
            Log.e("LDS \\ READ VOUCHERS", e.toString());
            Hashtable<Integer, ArrayList<VoucherModel>> emptyTable = new Hashtable<>();
            emptyTable.put(0, new ArrayList<VoucherModel>());
            emptyTable.put(1, new ArrayList<VoucherModel>());
            return new Result.Error<>(emptyTable);
        }
    }

    public void storeReceipts(Context appContext, ReceiptModel[] receipts) {
        try {
            ObjectOutput objOut = new ObjectOutputStream(new FileOutputStream(new File(appContext.getFilesDir(), "")
                    + File.separator + receiptsFilePath));
            objOut.writeObject(receipts);
            objOut.close();
        }
        catch (Exception e) {
            Log.e("LDS \\ STORE RECEIPTS", e.toString());
        }
    }

    public Result<ReceiptModel[]> getReceipts(Context appContext) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(new File(appContext.getFilesDir(), "")
                    + File.separator + receiptsFilePath)));
            ReceiptModel[] receipts = (ReceiptModel[]) inputStream.readObject();
            inputStream.close();

            return new Result.Success<>(receipts);
        }
        catch (Exception e) {
            Log.e("LDS \\ READ RECEIPTS", e.toString());
            return new Result.Error<>(new ReceiptModel[0]);
        }
    }

    public void storeItems(Context appContext, ItemModel[] items) {
        try {
            ObjectOutput objOut = new ObjectOutputStream(new FileOutputStream(new File(appContext.getFilesDir(), "")
                    + File.separator + itemsFilePath));
            objOut.writeObject(items);
            objOut.close();
        }
        catch (Exception e) {
            Log.e("LDS \\ STORE ITEMS", e.toString());
        }
    }

    public Result<ItemModel[]> getItems(Context appContext) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(new File(appContext.getFilesDir(), "")
                + File.separator + itemsFilePath)));
            ItemModel[] items = (ItemModel[]) inputStream.readObject();
            inputStream.close();

            return new Result.Success<>(items);
        }
        catch (Exception e) {
            Log.e("LDS \\ READ ITEMS", e.toString());
            return new Result.Error<>(new ItemModel[0]);
        }
    }

    public Result<String> getUserID(Context appContext) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(
                appContext.getResources().getString(R.string.user_id_preferences_file), Context.MODE_PRIVATE);
        String storedUserID = sharedPreferences.getString(appContext.getResources().getString(R.string.user_id),
                appContext.getResources().getString(R.string.empty));

        return new Result.Success<>(storedUserID);
    }
}
