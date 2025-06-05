package com.example.movieapp.model.auth;

import com.google.gson.annotations.SerializedName;

public class AccountResponse {
    @SerializedName("id")
    private int id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("avatar")
    private Avatar avatar;
    
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getName() {
        return name;
    }
    
    public Avatar getAvatar() {
        return avatar;
    }
    
    public static class Avatar {
        @SerializedName("gravatar")
        private Gravatar gravatar;
        
        @SerializedName("tmdb")
        private Tmdb tmdb;
        
        public Gravatar getGravatar() {
            return gravatar;
        }
        
        public Tmdb getTmdb() {
            return tmdb;
        }
    }
    
    public static class Gravatar {
        @SerializedName("hash")
        private String hash;
        
        public String getHash() {
            return hash;
        }
    }
    
    public static class Tmdb {
        @SerializedName("avatar_path")
        private String avatarPath;
        
        public String getAvatarPath() {
            return avatarPath;
        }
    }
} 