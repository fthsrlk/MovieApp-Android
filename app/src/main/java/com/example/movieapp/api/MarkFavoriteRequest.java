package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

public class MarkFavoriteRequest {
    @SerializedName("media_type")
    private String mediaType; // "movie" veya "tv"

    @SerializedName("media_id")
    private int mediaId;

    @SerializedName("favorite")
    private boolean favorite;

    public MarkFavoriteRequest(String mediaType, int mediaId, boolean favorite) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.favorite = favorite;
    }
} 