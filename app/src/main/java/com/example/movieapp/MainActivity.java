// MainActivity.java

package com.example.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieapp.adapter.MovieAdapter;
import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.api.StatusResponse;
import com.example.movieapp.api.VideoResponse;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.Movie;
import com.example.movieapp.service.RecommendationService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.movieapp.decoration.DynamicItemAnimator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private DatabaseHelper db;
    private List<Movie> movieList;
    private ExtendedFloatingActionButton fabAdd;
    private MaterialToolbar toolbar;
    private TMDbApi movieService;
    private ProgressDialog progressDialog;

    // Özel bir bayrak ekleyerek sadece etkinlik ilk oluşturulduğunda filmler sync edilsin
    private boolean initialLoadDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Kullanıcı oturum bilgisini al
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int accountId = prefs.getInt("account_id", -1);
        
        // UI elemanlarını tanımla
        recyclerView = findViewById(R.id.recyclerViewMovies);
        fabAdd = findViewById(R.id.fabAddMovie);
        toolbar = findViewById(R.id.toolbar);
        
        // İlerleme diyalogu
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Filmler yükleniyor...");
        progressDialog.setCancelable(false);
        
        // RecyclerView ayarları
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Kaydırma animasyonunu ekle
        recyclerView.addItemDecoration(new DynamicItemAnimator());
        
        // Yumuşak kaydırma efekti
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        
        // Özel scroll listener ekle
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView.invalidateItemDecorations(); // Animasyonları güncelle
            }
        });
        
        // Film listesi ve adapter
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList, new MovieAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra("movie_id", movie.getId());
                intent.putExtra("movie_title", movie.getTitle());
                intent.putExtra("movie_poster", movie.getPosterPath());
                intent.putExtra("movie_overview", movie.getDescription());
                
                startActivity(intent);
            }
            
            @Override
            public void onWatchClick(Movie movie) {
                // Video URL'sini al
                TMDbApi apiService = ApiClient.getClient().create(TMDbApi.class);
                apiService.getMovieVideos(movie.getId(), "tr-TR").enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<VideoResponse.Video> videos = response.body().getResults();
                            if (videos != null && !videos.isEmpty()) {
                                // İlk YouTube videosunu bul
                                for (VideoResponse.Video video : videos) {
                                    if (video.getSite().equalsIgnoreCase("YouTube")) {
                                        openYouTube(video.getKey());
                                        return;
                                    }
                                }
                            }
                            Toast.makeText(MainActivity.this, "Video bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Video yüklenemedi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onEditClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, EditMovieActivity.class);
                intent.putExtra("movie_id", movie.getId());
                startActivity(intent);
            }
        });
        
        recyclerView.setAdapter(movieAdapter);
        
        // FAB tıklama olayı
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditMovieActivity.class);
            startActivity(intent);
        });
        
        // Veritabanı
        db = new DatabaseHelper(this, accountId);
        movieService = ApiClient.getClient().create(TMDbApi.class);
        
        // Toolbar ayarları
        setSupportActionBar(toolbar);
        
        // Filmleri yükle
        loadMoviesFromDb();
    }
    
    private void loadMoviesFromDb() {
        // Veritabanından filmleri yükle
        movieList.clear();
        movieList.addAll(db.getAllMovies());
        movieAdapter.notifyDataSetChanged();
        
        // İlk açılışta sync işlemi yap
        if (!initialLoadDone) {
            syncMoviesWithRecommendationSystem(movieList);
            initialLoadDone = true;
        }
    }
    
    /**
     * Filmleri öneri sistemine sync et
     */
    private void syncMoviesWithRecommendationSystem(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return;
        }
        
        RecommendationService recommendationService = new RecommendationService(this);
        final int totalItems = movies.size();
        final AtomicInteger processedItems = new AtomicInteger(0);
        
        // Her film için
        for (Movie movie : movies) {
            // Kullanıcının eklediği içerik olarak gönder
            Map<String, Object> content = new HashMap<>();
            content.put("item_id", movie.getId());
            content.put("title", movie.getTitle());
            content.put("content_type", "movie");
            content.put("overview", movie.getDescription());
            content.put("poster_path", movie.getPosterPath());
            
            // Kullanıcı ID'si varsayılan olarak 1
            recommendationService.addUserContent(1, content, new RecommendationService.ServiceCallback<StatusResponse>() {
                @Override
                public void onSuccess(StatusResponse response) {
                    Log.d(TAG, "Film öneri sistemine eklendi: " + movie.getTitle());
                    
                    // Önemli: Film eklendikten hemen sonra değerlendirme ekle
                    // Değerlendirmenin filme göre durumunu hesapla (film hakkında bilgiye göre)
                    float rating = 5.0f; // Varsayılan olarak en yüksek puan
                    
                    // Film değerlendirmesini ekle
                    recommendationService.addRating(1, movie.getId(), rating, new RecommendationService.ServiceCallback<StatusResponse>() {
                        @Override
                        public void onSuccess(StatusResponse response) {
                            Log.d(TAG, "Film değerlendirmesi eklendi: " + movie.getTitle() + " - " + rating + "/5");
                            
                            // Son film işlendiğinde, modeli eğit
                            if (processedItems.incrementAndGet() == totalItems) {
                                trainModelAfterDelay(recommendationService);
                            }
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Film değerlendirmesi eklenirken hata: " + errorMessage);
                            
                            // Hata olsa bile sayacı artır
                            if (processedItems.incrementAndGet() == totalItems) {
                                trainModelAfterDelay(recommendationService);
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Film öneri sistemine eklenirken hata: " + errorMessage);
                    
                    // Hata olsa bile sayacı artır
                    if (processedItems.incrementAndGet() == totalItems) {
                        trainModelAfterDelay(recommendationService);
                    }
                }
            });
        }
    }
    
    /**
     * Gecikme sonrasında modeli eğitir
     * 
     * @param recommendationService Öneri servisi
     */
    private void trainModelAfterDelay(RecommendationService recommendationService) {
        // Tüm filmler eklendikten sonra 2 saniye bekle ve modeli eğit
        new Handler().postDelayed(() -> {
            recommendationService.trainUserModel(1, new RecommendationService.ServiceCallback<StatusResponse>() {
                @Override
                public void onSuccess(StatusResponse response) {
                    Log.d(TAG, "Öneri modeli başarıyla eğitildi");
                    
                    // Modelin ilk kez eğitildiğini kontrol et
                    SharedPreferences prefs = getSharedPreferences("app_preferences", MODE_PRIVATE);
                    boolean firstLaunch = prefs.getBoolean("first_launch", true);
                    
                    if (firstLaunch) {
                        // İlk açılışta mesaj göster
                        Toast.makeText(MainActivity.this, "Öneriler güncellendi", Toast.LENGTH_SHORT).show();
                        
                        // İlk açılış bayrağını kaldır
                        prefs.edit().putBoolean("first_launch", false).apply();
                    }
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Öneri modeli eğitilirken hata: " + errorMessage);
                }
            });
        }, 2000); // 2 saniye gecikme
    }
    
    private void openYouTube(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesFromDb();
    }
}
