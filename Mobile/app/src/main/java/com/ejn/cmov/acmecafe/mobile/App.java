package com.ejn.cmov.acmecafe.mobile;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.executorService = Executors.newFixedThreadPool(4);
    }
}