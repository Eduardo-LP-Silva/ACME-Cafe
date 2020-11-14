package com.ejn.cmov.acmecafe.terminal;

public interface Callback<T> {
    void onComplete(Result<T> result);

    void onComplete(Result.Error<String> error);
}
