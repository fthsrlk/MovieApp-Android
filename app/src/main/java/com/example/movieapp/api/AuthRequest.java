package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Öneri API'si kimlik doğrulama isteği için model sınıfı
 */
public class AuthRequest {
    
    @SerializedName("user_id")
    private final int userId;
    
    @SerializedName("api_key")
    private final String apiKey;
    
    /**
     * Kimlik doğrulama isteği oluşturur
     * 
     * @param userId Kullanıcı ID
     * @param apiKey API anahtarı
     */
    public AuthRequest(int userId, String apiKey) {
        this.userId = userId;
        this.apiKey = apiKey;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getApiKey() {
        return apiKey;
    }
} 