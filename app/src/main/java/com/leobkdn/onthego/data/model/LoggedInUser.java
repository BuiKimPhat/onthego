package com.leobkdn.onthego.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String token;

    public LoggedInUser(String userId, String displayName, String token) {
        this.userId = userId;
        this.displayName = displayName;
        this.token = token;
    }
    public LoggedInUser() {
        this.userId = null;
        this.displayName = null;
        this.token = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return displayName;
    }
}