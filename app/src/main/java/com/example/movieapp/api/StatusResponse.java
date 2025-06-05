package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * API durum yanıtı için model sınıfı
 */
public class StatusResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("error")
    private String error;
    
    @SerializedName("status_code")
    private int statusCode;
    
    @SerializedName("status_message")
    private String statusMessage;
    
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getError() {
        return error;
    }
    
    /**
     * İşlemin başarılı olup olmadığını kontrol eder
     */
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status) || "ok".equalsIgnoreCase(status);
    }
    
    /**
     * Durum kodunu döndürür (TMDb API uyumluluğu için)
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Durum mesajını döndürür (TMDb API uyumluluğu için)
     */
    public String getStatusMessage() {
        return statusMessage != null ? statusMessage : message;
    }
} 