package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * API durum kontrolü yanıtı için model sınıfı
 */
public class HealthResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("api_version")
    private String apiVersion;
    
    @SerializedName("models")
    private Map<String, Boolean> models;
    
    @SerializedName("data")
    private Map<String, Boolean> data;
    
    @SerializedName("error")
    private String error;
    
    public String getStatus() {
        return status;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public Map<String, Boolean> getModels() {
        return models;
    }
    
    public Map<String, Boolean> getData() {
        return data;
    }
    
    public String getError() {
        return error;
    }
    
    /**
     * API'nin sağlıklı olup olmadığını kontrol eder
     */
    public boolean isHealthy() {
        return "ok".equalsIgnoreCase(status);
    }
    
    /**
     * Belirli bir modelin yüklü olup olmadığını kontrol eder
     * 
     * @param modelName Model adı ("collaborative", "content_based", "hybrid")
     * @return Model yüklü ise true, değilse false
     */
    public boolean isModelLoaded(String modelName) {
        return models != null && models.containsKey(modelName) && models.get(modelName);
    }
    
    /**
     * Belirli bir veri setinin yüklü olup olmadığını kontrol eder
     * 
     * @param dataName Veri seti adı ("items", "ratings")
     * @return Veri seti yüklü ise true, değilse false
     */
    public boolean isDataLoaded(String dataName) {
        return data != null && data.containsKey(dataName) && data.get(dataName);
    }
} 