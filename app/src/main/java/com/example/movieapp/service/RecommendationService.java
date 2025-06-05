package com.example.movieapp.service;

import android.content.Context;
import android.util.Log;

import com.example.movieapp.BuildConfig;
import com.example.movieapp.api.AuthRequest;
import com.example.movieapp.api.AuthResponse;
import com.example.movieapp.api.HealthResponse;
import com.example.movieapp.api.RecommendationApiClient;
import com.example.movieapp.api.RecommendationResponse;
import com.example.movieapp.api.SampleDataRequest;
import com.example.movieapp.api.SimilarItemsResponse;
import com.example.movieapp.api.StatusResponse;
import com.example.movieapp.api.TrainRequest;
import com.example.movieapp.api.RatingRequest;
import com.example.movieapp.api.UserContentRequest;
import com.example.movieapp.api.UserModelRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Map;

/**
 * Öneri sistemi servisi
 */
public class RecommendationService {
    
    private static final String TAG = "RecommendationService";
    
    private final RecommendationApiClient apiClient;
    private final Context context;
    
    /**
     * Öneri servisi oluşturur
     * 
     * @param context Uygulama bağlamı
     */
    public RecommendationService(Context context) {
        this.context = context;
        this.apiClient = RecommendationApiClient.getInstance(context);
    }
    
    /**
     * API durum kontrolü yapar
     * 
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void checkHealth(final ServiceCallback<HealthResponse> callback) {
        apiClient.getApi().checkHealth().enqueue(new Callback<HealthResponse>() {
            @Override
            public void onResponse(Call<HealthResponse> call, Response<HealthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("API durum kontrolü başarısız: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<HealthResponse> call, Throwable t) {
                callback.onError("API'ye bağlanılamadı: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kullanıcı kimlik doğrulama yapar
     * 
     * @param userId Kullanıcı ID
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void authenticate(int userId, final ServiceCallback<AuthResponse> callback) {
        String apiKey = BuildConfig.TMDB_API_KEY;
        
        AuthRequest request = new AuthRequest(userId, apiKey);
        
        apiClient.getApi().authenticate(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    if (authResponse.isSuccess()) {
                        // Token'ı kaydet
                        apiClient.setAuthToken(authResponse.getToken());
                        callback.onSuccess(authResponse);
                    } else {
                        callback.onError("Kimlik doğrulama başarısız: " + authResponse.getError());
                    }
                } else {
                    callback.onError("Kimlik doğrulama isteği başarısız: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Kimlik doğrulama sırasında hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kullanıcı için film/dizi önerileri alır
     * 
     * @param userId Kullanıcı ID
     * @param limit Öneri sayısı limiti
     * @param contentType İçerik tipi (movie, tv veya null)
     * @param strategy Öneri stratejisi (collaborative, content_based, hybrid)
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void getRecommendations(int userId, int limit, String contentType, String strategy, 
                                   final ServiceCallback<RecommendationResponse> callback) {
        
        apiClient.getApi().getRecommendations(userId, limit, contentType, strategy)
                .enqueue(new Callback<RecommendationResponse>() {
            @Override
            public void onResponse(Call<RecommendationResponse> call, Response<RecommendationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Öneri alma başarısız: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<RecommendationResponse> call, Throwable t) {
                callback.onError("Öneri alınırken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Belirli bir içeriğe benzer içerikleri alır
     * 
     * @param itemId İçerik ID
     * @param limit Benzer içerik sayısı limiti
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void getSimilarItems(int itemId, int limit, final ServiceCallback<SimilarItemsResponse> callback) {
        apiClient.getApi().getSimilarItems(itemId, limit).enqueue(new Callback<SimilarItemsResponse>() {
            @Override
            public void onResponse(Call<SimilarItemsResponse> call, Response<SimilarItemsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Benzer içerikler alınamadı: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<SimilarItemsResponse> call, Throwable t) {
                callback.onError("Benzer içerikler alınırken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Model eğitimini başlatır
     * 
     * @param modelType Model tipi
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void trainModels(String modelType, final ServiceCallback<StatusResponse> callback) {
        if (!apiClient.isAuthenticated()) {
            callback.onError("API kimlik doğrulaması gerekiyor");
            return;
        }
        
        TrainRequest request;
        switch (modelType) {
            case "collaborative":
                request = TrainRequest.trainCollaborative();
                break;
            case "content_based":
                request = TrainRequest.trainContentBased();
                break;
            case "hybrid":
                request = TrainRequest.trainHybrid();
                break;
            default:
                request = TrainRequest.trainAll();
                break;
        }
        
        apiClient.getApi().trainModels(apiClient.getAuthorizationHeader(), request)
                .enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Model eğitimi başarısız: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                callback.onError("Model eğitimi sırasında hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Örnek veri yüklemeyi başlatır
     * 
     * @param callback Başarı/hata durumu için geri çağrı
     */
    public void loadSampleData(final ServiceCallback<StatusResponse> callback) {
        if (!apiClient.isAuthenticated()) {
            callback.onError("API kimlik doğrulaması gerekiyor");
            return;
        }
        
        SampleDataRequest request = SampleDataRequest.defaultSampleData();
        
        apiClient.getApi().loadSampleData(apiClient.getAuthorizationHeader(), request)
                .enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Örnek veri yükleme başarısız: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                callback.onError("Örnek veri yüklenirken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kullanıcı değerlendirmesi ekler
     *
     * @param userId Kullanıcı ID
     * @param itemId İçerik ID
     * @param rating Değerlendirme puanı
     * @param callback Geri çağrı
     */
    public void addRating(int userId, int itemId, float rating, ServiceCallback<StatusResponse> callback) {
        RatingRequest request = new RatingRequest(userId, itemId, rating);
        
        apiClient.getApi().addRating(request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Değerlendirme eklenirken hata: " + 
                            (response.errorBody() != null ? parseError(response.errorBody()) : response.code());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                callback.onError("Değerlendirme eklenirken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kullanıcının eklediği içeriği öneri sistemine dahil eder
     *
     * @param userId Kullanıcı ID
     * @param content İçerik bilgileri
     * @param callback Geri çağrı
     */
    public void addUserContent(int userId, Map<String, Object> content, ServiceCallback<StatusResponse> callback) {
        UserContentRequest request = new UserContentRequest(userId, content);
        
        apiClient.getApi().addUserContent(request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "İçerik eklenirken hata: " + 
                            (response.errorBody() != null ? parseError(response.errorBody()) : response.code());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                callback.onError("İçerik eklenirken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Kullanıcı modelini eğitir
     *
     * @param userId Kullanıcı ID
     * @param callback Geri çağrı
     */
    public void trainUserModel(int userId, ServiceCallback<StatusResponse> callback) {
        UserModelRequest request = new UserModelRequest(userId);
        
        apiClient.getApi().trainUserModel(request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Model eğitilirken hata: " + 
                            (response.errorBody() != null ? parseError(response.errorBody()) : response.code());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                callback.onError("Model eğitilirken hata: " + t.getMessage());
            }
        });
    }
    
    /**
     * Hata yanıtını analiz eder
     * 
     * @param responseBody Yanıt gövdesi
     * @return Hata mesajı
     */
    private String parseError(okhttp3.ResponseBody responseBody) {
        try {
            String errorBody = responseBody.string();
            Log.e(TAG, "Hata gövdesi: " + errorBody);
            return errorBody;
        } catch (Exception e) {
            Log.e(TAG, "Hata analiz edilirken hata oluştu", e);
            return "Bilinmeyen hata";
        }
    }
    
    /**
     * Servis çağrıları için geri çağrı arayüzü
     * 
     * @param <T> Yanıt tipi
     */
    public interface ServiceCallback<T> {
        void onSuccess(T response);
        void onError(String errorMessage);
    }
} 