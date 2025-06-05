package com.example.movieapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Header;
import retrofit2.http.Body;

/**
 * Öneri sistemi API'si için Retrofit arayüzü
 */
public interface RecommendationApi {
    
    /**
     * Kullanıcı için film/dizi önerileri alır
     * 
     * @param userId Kullanıcı ID
     * @param limit Öneri sayısı limiti
     * @param contentType İçerik tipi (movie, tv veya null)
     * @param strategy Öneri stratejisi (collaborative, content_based, hybrid)
     * @return Öneriler listesi
     */
    @GET("recommendations/{user_id}")
    Call<RecommendationResponse> getRecommendations(
        @Path("user_id") int userId,
        @Query("limit") int limit,
        @Query("content_type") String contentType,
        @Query("strategy") String strategy
    );
    
    /**
     * Belirli bir içeriğe benzer içerikleri alır
     * 
     * @param itemId İçerik ID 
     * @param limit Öneri sayısı limiti
     * @return Benzer içerikler listesi
     */
    @GET("similar/{item_id}")
    Call<SimilarItemsResponse> getSimilarItems(
        @Path("item_id") int itemId,
        @Query("limit") int limit
    );
    
    /**
     * Kullanıcı kimlik doğrulama için
     * 
     * @param request Kimlik doğrulama isteği
     * @return Token yanıtı
     */
    @POST("auth")
    Call<AuthResponse> authenticate(
        @Body AuthRequest request
    );
    
    /**
     * API'nin durumunu kontrol etmek için
     * 
     * @return Durum yanıtı
     */
    @GET("health")
    Call<HealthResponse> checkHealth();
    
    /**
     * Model eğitimi için
     * 
     * @param token Yetkilendirme token'ı
     * @param request Eğitim isteği
     * @return Durum yanıtı
     */
    @POST("train")
    Call<StatusResponse> trainModels(
        @Header("Authorization") String token,
        @Body TrainRequest request
    );
    
    /**
     * Örnek veri yüklemek için
     * 
     * @param token Yetkilendirme token'ı
     * @param request Veri yükleme isteği
     * @return Durum yanıtı
     */
    @POST("load_sample_data")
    Call<StatusResponse> loadSampleData(
        @Header("Authorization") String token,
        @Body SampleDataRequest request
    );
    
    /**
     * Kullanıcı değerlendirmesini eklemek için
     * 
     * @param rating Değerlendirme bilgileri
     * @return Durum yanıtı
     */
    @POST("ratings")
    Call<StatusResponse> addRating(
        @Body RatingRequest rating
    );
    
    /**
     * Kullanıcının eklediği içeriği öneri sistemine dahil etmek için
     * 
     * @param request Kullanıcı içeriği
     * @return Durum yanıtı
     */
    @POST("user_content")
    Call<StatusResponse> addUserContent(
        @Body UserContentRequest request
    );
    
    /**
     * Kullanıcı modeli eğitmek için
     * 
     * @param request Eğitim isteği
     * @return Durum yanıtı
     */
    @POST("train_user_model")
    Call<StatusResponse> trainUserModel(
        @Body UserModelRequest request
    );
} 