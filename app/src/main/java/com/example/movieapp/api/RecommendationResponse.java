package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Öneri API'sinden gelen yanıt için model sınıfı
 */
public class RecommendationResponse {
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("strategy")
    private String strategy;
    
    @SerializedName("recommendations")
    private List<RecommendedItem> recommendations;
    
    public int getUserId() {
        return userId;
    }
    
    public String getStrategy() {
        return strategy;
    }
    
    public List<RecommendedItem> getRecommendations() {
        return recommendations;
    }
    
    /**
     * Önerilen içerik için model sınıfı
     */
    public static class RecommendedItem {
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
        
        @SerializedName("info")
        private Map<String, Object> infoMap;
        
        public int getItemId() {
            return itemId;
        }
        
        public float getScore() {
            return score;
        }
        
        public String getTitle() {
            // Eğer title direkt olarak mevcut değilse ve info nesnesi varsa oradan al
            if ((title == null || title.isEmpty()) && infoMap != null && infoMap.containsKey("title")) {
                Object titleObj = infoMap.get("title");
                return titleObj != null ? String.valueOf(titleObj) : "";
            }
            return title != null ? title : "";
        }
        
        public String getContentType() {
            // Eğer contentType direkt olarak mevcut değilse ve info nesnesi varsa oradan al
            if ((contentType == null || contentType.isEmpty()) && infoMap != null) {
                // İçeriğin tipini belirten farklı alanları kontrol et
                if (infoMap.containsKey("content_type")) {
                    Object contentTypeObj = infoMap.get("content_type");
                    return contentTypeObj != null ? String.valueOf(contentTypeObj) : "";
                } else if (infoMap.containsKey("media_type")) {
                    Object mediaTypeObj = infoMap.get("media_type");
                    return mediaTypeObj != null ? String.valueOf(mediaTypeObj) : "";
                } else if (infoMap.containsKey("type")) {
                    Object typeObj = infoMap.get("type");
                    return typeObj != null ? String.valueOf(typeObj) : "";
                }
            }
            return contentType != null ? contentType : "";
        }
        
        public String getPosterPath() {
            // Eğer posterPath direkt olarak mevcut değilse ve info nesnesi varsa oradan al
            if ((posterPath == null || posterPath.isEmpty()) && infoMap != null && infoMap.containsKey("poster_path")) {
                Object posterObj = infoMap.get("poster_path");
                return posterObj != null ? String.valueOf(posterObj) : "";
            }
            return posterPath != null ? posterPath : "";
        }
        
        public String getOverview() {
            // Eğer overview direkt olarak mevcut değilse ve info nesnesi varsa oradan al
            if ((overview == null || overview.isEmpty()) && infoMap != null && infoMap.containsKey("overview")) {
                Object overviewObj = infoMap.get("overview");
                if (overviewObj != null) {
                    String overviewStr = String.valueOf(overviewObj);
                    if (!overviewStr.equals("null") && !overviewStr.equals("NaN")) {
                        return overviewStr;
                    }
                }
            }
            return overview != null ? overview : "";
        }
        
        /**
         * Bu içeriğin bir film olup olmadığını kontrol eder
         */
        public boolean isMovie() {
            String type = getContentType().toLowerCase();
            return "movie".equals(type) || "film".equals(type);
        }
        
        /**
         * Bu içeriğin bir dizi olup olmadığını kontrol eder
         */
        public boolean isTvShow() {
            String type = getContentType().toLowerCase();
            return "tv".equals(type) || "dizi".equals(type) || "tv_series".equals(type) || "tv_show".equals(type);
        }
        
        /**
         * Tam poster URL'sini döndürür
         */
        public String getFullPosterPath() {
            String path = getPosterPath();
            if (path == null || path.isEmpty()) {
                return null;
            }
            
            // Eğer zaten tam URL ise olduğu gibi döndür
            if (path.startsWith("http://") || path.startsWith("https://")) {
                return path;
            }
            
            // URL değilse, TMDB base URL'ini ekle
            return "https://image.tmdb.org/t/p/w500" + path;
        }
    }
} 