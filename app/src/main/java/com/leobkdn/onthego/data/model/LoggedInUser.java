package com.leobkdn.onthego.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String email;
    private String token;

    public LoggedInUser(String displayName, String token) {
        this.displayName = displayName;
        this.token = token;
    }

    public LoggedInUser(String displayName, String email, String token) {
        this.displayName = displayName;
        this.email = email;
        this.token = token;
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