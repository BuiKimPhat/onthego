package com.leobkdn.onthego.ui.login;

import com.leobkdn.onthego.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String email;
    private String token; // storage purpose only (invisible to UI)

    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    public LoggedInUserView(String displayName, String email, String token) {
        this.displayName = displayName;
        this.email = email;
        this.token = token;
    }
    public LoggedInUserView() {
        this.displayName = null;
        this.email = null;
        this.token = null;
    }
    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return displayName;
    }
}