package com.example.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Örnek veri yükleme isteği için model sınıfı
 */
public class SampleDataRequest {
    
    @SerializedName("n_movies")
    private final int nMovies;
    
    @SerializedName("n_tv")
    private final int nTv;
    
    @SerializedName("n_users")
    private final int nUsers;
    
    @SerializedName("min_ratings_per_user")
    private final int minRatingsPerUser;
    
    @SerializedName("max_ratings_per_user")
    private final int maxRatingsPerUser;
    
    /**
     * Örnek veri yükleme isteği oluşturur
     * 
     * @param nMovies Film sayısı
     * @param nTv Dizi sayısı
     * @param nUsers Kullanıcı sayısı
     * @param minRatingsPerUser Kullanıcı başına minimum değerlendirme sayısı
     * @param maxRatingsPerUser Kullanıcı başına maksimum değerlendirme sayısı
     */
    public SampleDataRequest(int nMovies, int nTv, int nUsers, int minRatingsPerUser, int maxRatingsPerUser) {
        this.nMovies = nMovies;
        this.nTv = nTv;
        this.nUsers = nUsers;
        this.minRatingsPerUser = minRatingsPerUser;
        this.maxRatingsPerUser = maxRatingsPerUser;
    }
    
    /**
     * Varsayılan değerlerle örnek veri yükleme isteği oluşturur
     */
    public static SampleDataRequest defaultSampleData() {
        return new SampleDataRequest(100, 100, 50, 5, 20);
    }
    
    /**
     * Daha küçük bir veri seti için örnek veri yükleme isteği oluşturur
     */
    public static SampleDataRequest smallSampleData() {
        return new SampleDataRequest(50, 50, 20, 3, 10);
    }
    
    /**
     * Sadece filmler için örnek veri yükleme isteği oluşturur
     */
    public static SampleDataRequest onlyMovies(int count) {
        return new SampleDataRequest(count, 0, 30, 5, 15);
    }
    
    /**
     * Sadece diziler için örnek veri yükleme isteği oluşturur
     */
    public static SampleDataRequest onlyTvShows(int count) {
        return new SampleDataRequest(0, count, 30, 5, 15);
    }
    
    public int getNMovies() {
        return nMovies;
    }
    
    public int getNTv() {
        return nTv;
    }
    
    public int getNUsers() {
        return nUsers;
    }
    
    public int getMinRatingsPerUser() {
        return minRatingsPerUser;
    }
    
    public int getMaxRatingsPerUser() {
        return maxRatingsPerUser;
    }
} 