package com.ejn.cmov.acmecafe.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ejn.cmov.acmecafe.mobile.R;
import com.ejn.cmov.acmecafe.mobile.data.Result;
import com.ejn.cmov.acmecafe.mobile.data.model.ItemModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class LocalDataSource {
    final String itemsFilePath = "menu_items.srl";

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
            return new Result.Error<ItemModel[]>(new ItemModel[0]);
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
