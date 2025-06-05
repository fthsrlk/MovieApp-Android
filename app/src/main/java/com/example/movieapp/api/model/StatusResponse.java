package com.example.movieapp.api.model;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("status_code")
    private int statusCode;
    
    @SerializedName("status_message")
    private String statusMessage;

    public boolean isSuccess() {
        return success;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getStatusMessage() {
        return statusMessage;
    }
} 