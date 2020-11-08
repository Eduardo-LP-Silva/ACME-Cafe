package com.ejn.cmov.acmecafe.mobile.data;

public interface Callback<T> {
    void onComplete(Result<T> result);
}
