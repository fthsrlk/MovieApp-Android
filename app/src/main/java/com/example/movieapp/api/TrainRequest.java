package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Model eğitimi isteği için model sınıfı
 */
public class TrainRequest {
    
    @SerializedName("model_type")
    private final String modelType;
    
    @SerializedName("params")
    private final ModelParams params;
    
    /**
     * Model eğitimi isteği oluşturur
     * 
     * @param modelType Model tipi ("collaborative", "content_based", "hybrid", "all")
     * @param params Model parametreleri
     */
    public TrainRequest(String modelType, ModelParams params) {
        this.modelType = modelType;
        this.params = params;
    }
    
    /**
     * Tüm modelleri varsayılan parametrelerle eğitim isteği oluşturur
     */
    public static TrainRequest trainAll() {
        return new TrainRequest("all", new ModelParams());
    }
    
    /**
     * İşbirlikçi filtreleme modelini eğitim isteği oluşturur
     */
    public static TrainRequest trainCollaborative() {
        return new TrainRequest("collaborative", new ModelParams());
    }
    
    /**
     * İçerik tabanlı filtreleme modelini eğitim isteği oluşturur
     */
    public static TrainRequest trainContentBased() {
        return new TrainRequest("content_based", new ModelParams());
    }
    
    /**
     * Hibrit modeli eğitim isteği oluşturur
     */
    public static TrainRequest trainHybrid() {
        return new TrainRequest("hybrid", new ModelParams());
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public ModelParams getParams() {
        return params;
    }
    
    /**
     * Model eğitimi parametreleri için model sınıfı
     */
    public static class ModelParams {
        @SerializedName("n_factors")
        private int nFactors = 50;
        
        @SerializedName("n_epochs")
        private int nEpochs = 20;
        
        @SerializedName("learning_rate")
        private double learningRate = 0.005;
        
        @SerializedName("regularization")
        private double regularization = 0.02;
        
        @SerializedName("random_state")
        private int randomState = 42;
        
        @SerializedName("test_size")
        private double testSize = 0.2;
        
        public ModelParams() {
            // Varsayılan değerlerle oluştur
        }
        
        public ModelParams setNFactors(int nFactors) {
            this.nFactors = nFactors;
            return this;
        }
        
        public ModelParams setNEpochs(int nEpochs) {
            this.nEpochs = nEpochs;
            return this;
        }
        
        public ModelParams setLearningRate(double learningRate) {
            this.learningRate = learningRate;
            return this;
        }
        
        public ModelParams setRegularization(double regularization) {
            this.regularization = regularization;
            return this;
        }
        
        public ModelParams setRandomState(int randomState) {
            this.randomState = randomState;
            return this;
        }
        
        public ModelParams setTestSize(double testSize) {
            this.testSize = testSize;
            return this;
        }
    }
} 