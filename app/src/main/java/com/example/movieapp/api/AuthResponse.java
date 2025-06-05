package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Öneri API'si kimlik doğrulama yanıtı için model sınıfı
 */
public class AuthResponse {
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("expires_at")
    private String expiresAt;
    
    @SerializedName("error")
    private String error;
    
    public String getToken() {
        return token;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    public String getError() {
        return error;
    }
    
    /**
     * Kimlik doğrulamanın başarılı olup olmadığını kontrol eder
     */
    public boolean isSuccess() {
        return token != null && !token.isEmpty();
    }
    
    /**
     * Bearer token formatında yetkilendirme başlığı döndürür
     */
    public String getAuthorizationHeader() {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return "Bearer " + token;
    }
} 