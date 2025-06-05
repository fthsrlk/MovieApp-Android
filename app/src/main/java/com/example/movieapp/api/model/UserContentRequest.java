package com.example.movieapp.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Kullanıcı içeriği isteği modeli
 */
public class UserContentRequest {
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("content")
    private Map<String, Object> content;
    
    public UserContentRequest(int userId, Map<String, Object> content) {
        this.userId = userId;
        this.content = content;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Map<String, Object> getContent() {
        return content;
    }
    
    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
} 