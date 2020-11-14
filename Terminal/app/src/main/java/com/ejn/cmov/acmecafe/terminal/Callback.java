package com.ejn.cmov.acmecafe.terminal;

import com.ejn.cmov.acmecafe.terminal.ReceiptModel;
import com.ejn.cmov.acmecafe.terminal.Result;

public interface Callback<T> {
    void onComplete(Result<T> result);

    void onComplete(Result.Error<ReceiptModel[]> error);
}
