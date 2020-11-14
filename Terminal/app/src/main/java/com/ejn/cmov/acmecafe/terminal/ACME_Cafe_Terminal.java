package com.ejn.cmov.acmecafe.terminal;

import android.app.Application;
import android.content.Context;

public class ACME_Cafe_Terminal extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ACME_Cafe_Terminal.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ACME_Cafe_Terminal.context;
    }

}
