package com.example.movieapp.model.auth;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("request_token")
    private String requestToken;
    
    @SerializedName("expires_at")
    private String expiresAt;
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getRequestToken() {
        return requestToken;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
} 