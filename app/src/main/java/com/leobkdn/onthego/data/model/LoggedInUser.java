package com.leobkdn.onthego.data.model;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.Date;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String email;
    private String token;
    private boolean isAdmin;
    private Date birthday;
    private String address;

    public LoggedInUser(String displayName, String email, String token, boolean isAdmin, @Nullable Date birthday, @Nullable String address) {
        this.displayName = displayName;
        this.email = email;
        this.token = token;
        this.isAdmin = isAdmin;
        this.birthday = birthday;
        this.address = address;
    }

    public LoggedInUser() {
        displayName = "An văn";
        email = "Andeptrai89@gmail.com";
        token = null;
        isAdmin = true;
        birthday = new Date();
        address = "Bắc Ninh";
    }

    @Nullable
    public Date getBirthday() {
        return birthday;
    }

    @Nullable
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setToken(String token) {
        this.token = token;
    }
}