package com.example.movieapp.model.auth;

import com.google.gson.annotations.SerializedName;

public class SessionResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("session_id")
    private String sessionId;
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getSessionId() {
        return sessionId;
    }
} 