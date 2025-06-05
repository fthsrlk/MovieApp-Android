package com.example.movieapp.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("request_token")
    private String requestToken;
    
    public LoginRequest(String username, String password, String requestToken) {
        this.username = username;
        this.password = password;
        this.requestToken = requestToken;
    }
} 