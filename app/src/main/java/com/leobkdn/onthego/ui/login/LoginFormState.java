package com.leobkdn.onthego.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */

// Manage input error messages
public class LoginFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer nameError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer nameError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.nameError = nameError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.nameError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getNameError(){ return nameError; }

    public boolean isDataValid() {
        return isDataValid;
    }
}