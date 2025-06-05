package com.example.movieapp.model;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("status_message")
    private String statusMessage;

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
} 