package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {
    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private List<Video> results;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
    
    public static class Video {
        @SerializedName("id")
        private String id;
        
        @SerializedName("key")
        private String key;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("site")
        private String site;
        
        @SerializedName("size")
        private int size;
        
        @SerializedName("type")
        private String type;
        
        @SerializedName("iso_639_1")
        private String languageCode;
        
        @SerializedName("iso_3166_1")
        private String countryCode;
        
        @SerializedName("official")
        private boolean official;
        
        public String getId() {
            return id;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getName() {
            return name;
        }
        
        public String getSite() {
            return site;
        }
        
        public int getSize() {
            return size;
        }
        
        public String getType() {
            return type;
        }
        
        public String getLanguageCode() {
            return languageCode;
        }
        
        public String getCountryCode() {
            return countryCode;
        }
        
        public boolean isOfficial() {
            return official;
        }
    }
} 