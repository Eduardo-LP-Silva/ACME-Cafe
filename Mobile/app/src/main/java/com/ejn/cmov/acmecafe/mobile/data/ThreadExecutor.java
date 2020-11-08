package com.ejn.cmov.acmecafe.mobile.data;

import java.util.concurrent.Executor;

public class ThreadExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
