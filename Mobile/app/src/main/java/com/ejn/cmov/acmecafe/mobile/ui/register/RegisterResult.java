package com.ejn.cmov.acmecafe.mobile.ui.register;

import androidx.annotation.Nullable;

public class RegisterResult {
    /* Success view
    @Nullable
    private * success;
    */
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    // RegisterResult(@Nullable * success) {
    //        this.success = success;
    //    }

    /*
    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }
     */

    @Nullable
    Integer getError() {
        return error;
    }
}
