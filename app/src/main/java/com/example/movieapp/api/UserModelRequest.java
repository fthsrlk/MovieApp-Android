package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Kullanıcı modeli eğitim isteği
 */
public class UserModelRequest {
    
    @SerializedName("user_id")
    private int userId;
    
    public UserModelRequest(int userId) {
        this.userId = userId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
} 