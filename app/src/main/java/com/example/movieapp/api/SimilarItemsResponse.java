package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Benzer içerikler API'sinden gelen yanıt için model sınıfı
 */
public class SimilarItemsResponse {
    
    @SerializedName("item_id")
    private int itemId;
    
    @SerializedName("similar_items")
    private List<SimilarItem> similarItems;
    
    public int getItemId() {
        return itemId;
    }
    
    public List<SimilarItem> getSimilarItems() {
        return similarItems;
    }
    
    /**
     * Benzer içerik için model sınıfı
     */
    public static class SimilarItem {
        @SerializedName("item_id")
        private int itemId;
        
        @SerializedName("score")
        private float score;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("content_type")
        private String contentType;
        
        @SerializedName("poster_path")
        private String posterPath;
        
        @SerializedName("overview")
        private String overview;
        
        public int getItemId() {
            return itemId;
        }
        
        public float getScore() {
            return score;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public String getPosterPath() {
            return posterPath;
        }
        
        public String getOverview() {
            return overview;
        }
        
        /**
         * Bu içeriğin bir film olup olmadığını kontrol eder
         */
        public boolean isMovie() {
            return "movie".equals(contentType);
        }
        
        /**
         * Bu içeriğin bir dizi olup olmadığını kontrol eder
         */
        public boolean isTvShow() {
            return "tv".equals(contentType);
        }
        
        /**
         * Tam poster URL'sini döndürür
         */
        public String getFullPosterPath() {
            if (posterPath == null || posterPath.isEmpty()) {
                return null;
            }
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
    }
} 