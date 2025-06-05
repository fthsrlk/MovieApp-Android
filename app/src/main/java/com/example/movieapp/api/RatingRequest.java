package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Kullanıcı değerlendirme isteği modeli
 */
public class RatingRequest {
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("item_id")
    private int itemId;
    
    @SerializedName("rating")
    private float rating;
    
    public RatingRequest(int userId, int itemId, float rating) {
        this.userId = userId;
        this.itemId = itemId;
        this.rating = rating;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public float getRating() {
        return rating;
    }
    
    public void setRating(float rating) {
        this.rating = rating;
    }
} 