package com.ejn.cmov.acmecafe.mobile.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer nameError;
    @Nullable
    private Integer nifError;
    @Nullable
    private Integer cardNoError;
    @Nullable
    private Integer expirationDateError;
    @Nullable
    private Integer cvvError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmPasswordError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameError, @Nullable Integer nameError, @Nullable Integer nifError,
                      @Nullable Integer cardNoError, @Nullable Integer expirationDateError, @Nullable Integer cvvError,
                      @Nullable Integer passwordError, @Nullable Integer confirmPasswordError) {
        this.usernameError = usernameError;
        this.nameError = nameError;
        this.nifError = nifError;
        this.cardNoError = cardNoError;
        this.expirationDateError = expirationDateError;
        this.cvvError = cvvError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.usernameError = null;
        this.nameError = null;
        this.nifError = null;
        this.cardNoError = null;
        this.expirationDateError = null;
        this.cvvError = null;
        this.passwordError = null;
        this.confirmPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getNifError() {
        return nifError;
    }

    @Nullable
    public Integer getCardNoError() {
        return cardNoError;
    }

    @Nullable
    public Integer getExpirationDateError() {
        return expirationDateError;
    }

    @Nullable
    public Integer getCvvError() {
        return cvvError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
