package com.leobkdn.onthego.ui.login;

import androidx.annotation.Nullable;

import com.leobkdn.onthego.data.model.LoggedInUser;

import java.time.LocalDate;
import java.util.Date;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String email;
    private boolean isAdmin;
    private String token; // storage purpose only (invisible to UI)
    private Date birthday; //profile
    private String address;

    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    public LoggedInUserView(String displayName, String email, @Nullable String token, boolean isAdmin, @Nullable Date birthday, @Nullable String address) {
        this.displayName = displayName;
        this.email = email;
        this.token = token;
        this.isAdmin = isAdmin;
        this.birthday = birthday;
        this.address = address;
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

    public Date getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public boolean getIsAdmin(){ return isAdmin; }

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