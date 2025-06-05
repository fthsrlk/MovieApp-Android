package com.example.movieapp.model.auth;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("request_token")
    private String requestToken;
    
    public TokenRequest(String requestToken) {
        this.requestToken = requestToken;
    }
} 