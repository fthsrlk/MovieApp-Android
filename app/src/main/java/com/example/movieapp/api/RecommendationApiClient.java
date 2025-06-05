package com.example.movieapp.api;

import android.content.Context;
import android.util.Log;

import com.example.movieapp.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Öneri API'si için istemci sınıfı
 */
public class RecommendationApiClient {
    
    private static final String TAG = "RecommendationApiClient";
    
    // Varsayılan URL - gerçek uygulamada yapılandırılabilir olmalı
    private static final String API_BASE_URL = "http://192.168.1.104:5000/api/";
    
    private static RecommendationApiClient instance;
    private final RecommendationApi recommendationApi;
    private String authToken;
    
    private RecommendationApiClient(String baseUrl) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        // JSON parser'ı daha hoşgörülü hale getir
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        
        recommendationApi = retrofit.create(RecommendationApi.class);
    }
    
    /**
     * Singleton instance alır
     * 
     * @param context Uygulama bağlamı
     * @return RecommendationApiClient instance
     */
    public static synchronized RecommendationApiClient getInstance(Context context) {
        if (instance == null) {
            // Gerçek uygulamada URL'yi SharedPreferences veya başka bir yapılandırma kaynağından alabiliriz
            String apiUrl = API_BASE_URL;
            instance = new RecommendationApiClient(apiUrl);
        }
        return instance;
    }
    
    /**
     * Singleton instance alır (URL ile)
     * 
     * @param baseUrl API temel URL'si
     * @return RecommendationApiClient instance
     */
    public static synchronized RecommendationApiClient getInstance(String baseUrl) {
        if (instance == null || !instance.getBaseUrl().equals(baseUrl)) {
            instance = new RecommendationApiClient(baseUrl);
        }
        return instance;
    }
    
    /**
     * API temel URL'sini alır
     * 
     * @return API temel URL'si
     */
    public String getBaseUrl() {
        return API_BASE_URL;
    }
    
    /**
     * API arayüzünü alır
     * 
     * @return RecommendationApi arayüzü
     */
    public RecommendationApi getApi() {
        return recommendationApi;
    }
    
    /**
     * Yetkilendirme token'ını ayarlar
     * 
     * @param token JWT token
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        Log.d(TAG, "Auth token set: " + (token != null));
    }
    
    /**
     * Yetkilendirme token'ını alır
     * 
     * @return Yetkilendirme token'ı veya null
     */
    public String getAuthToken() {
        return authToken;
    }
    
    /**
     * Bearer formatında yetkilendirme başlığını alır
     * 
     * @return Yetkilendirme başlığı veya null
     */
    public String getAuthorizationHeader() {
        if (authToken == null || authToken.isEmpty()) {
            return null;
        }
        return "Bearer " + authToken;
    }
    
    /**
     * Yetkilendirmenin yapılıp yapılmadığını kontrol eder
     * 
     * @return Yetkilendirme yapıldıysa true, değilse false
     */
    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }
} 