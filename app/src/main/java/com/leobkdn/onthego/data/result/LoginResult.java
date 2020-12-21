package com.leobkdn.onthego.data.result;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.model.LoggedInUser;

/**
 * Authentication result : success (user details) or error message.
 */

public class LoginResult {
    @Nullable
    private LoggedInUser success;
    @Nullable
    private String error;
    @Nullable
    private Result.Success<String> successString;

    public LoginResult(@Nullable String error) {
        this.error = error;
    }

    public LoginResult(@Nullable LoggedInUser success) {
        this.success = success;
    }

    public LoginResult(@Nullable Result.Success<String> successString){
        this.successString = successString;
    }

    @Nullable
    public LoggedInUser getSuccess() {
        return success;
    }

    @Nullable
    public String getError() {
        return error;
    }

    @Nullable
    public String getSuccessString(){ if (successString != null) return successString.getData(); return null;}
}