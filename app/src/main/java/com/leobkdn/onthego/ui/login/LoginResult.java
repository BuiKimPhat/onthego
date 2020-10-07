package com.leobkdn.onthego.ui.login;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.Result;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private String error;
    @Nullable
    private Result.Success<String> successString;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    LoginResult(@Nullable Result.Success<String> successString){
        this.successString = successString;
    }

    @Nullable
    public LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public String getSuccessString(){return successString.getData();}
}