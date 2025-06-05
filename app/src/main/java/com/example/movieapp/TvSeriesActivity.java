package com.example.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movieapp.adapter.TvSeriesAdapter;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.TvSeries;
import com.example.movieapp.service.RecommendationService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.api.VideoResponse;
import com.example.movieapp.api.TvResponse;
import com.example.movieapp.api.StatusResponse;
import android.app.ActivityOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.example.movieapp.decoration.DynamicItemAnimator;
import android.content.SharedPreferences;

public class TvSeriesActivity extends AppCompatActivity {
    private static final String TAG = "TvSeriesActivity";
    
    private RecyclerView recyclerView;
    private TvSeriesAdapter tvSeriesAdapter;
    private DatabaseHelper db;
    private List<TvSeries> tvSeriesList;
    private ExtendedFloatingActionButton fabAdd;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);

        int accountId = getSharedPreferences("auth", MODE_PRIVATE).getInt("account_id", -1);
        if (accountId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = new DatabaseHelper(this, accountId);

        // Toolbar ayarları
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dizilerim");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setText("Dizi Ekle");

        // Database
        tvSeriesList = new ArrayList<>();

        // RecyclerView ayarları
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        tvSeriesAdapter = new TvSeriesAdapter(this, tvSeriesList, new TvSeriesAdapter.OnTvSeriesClickListener() {
            @Override
            public void onTvSeriesClick(TvSeries tvSeries) {
                Intent intent = new Intent(TvSeriesActivity.this, TvSeriesDetailActivity.class);
                intent.putExtra("tvSeriesId", tvSeries.getId());
                
                View posterView = recyclerView.findViewWithTag(tvSeries.getId());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    TvSeriesActivity.this,
                    posterView,
                    "posterTransition"
                );
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onWatchClick(TvSeries tvSeries) {
                TMDbApi tmdbApi = ApiClient.getClient().create(TMDbApi.class);
                Call<VideoResponse> call = tmdbApi.getTvVideos(tvSeries.getTmdbId(), "tr-TR");
                call.enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            VideoResponse videoResponse = response.body();
                            if (videoResponse.getResults() != null && !videoResponse.getResults().isEmpty()) {
                                String videoKey = videoResponse.getResults().get(0).getKey();
                                if (videoKey != null && !videoKey.isEmpty()) {
                                    String youtubeLink = "https://www.youtube.com/watch?v=" + videoKey;
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink)));
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        Toast.makeText(TvSeriesActivity.this, "Video yüklenemedi", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEditClick(TvSeries tvSeries) {
                Intent intent = new Intent(TvSeriesActivity.this, EditTvSeriesActivity.class);
                intent.putExtra("tvSeriesId", tvSeries.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(tvSeriesAdapter);

        // FAB tıklama olayı
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(TvSeriesActivity.this, AddEditTvSeriesActivity.class);
            startActivity(intent);
        });

        // Dizileri yükle
        refreshTvSeries();
    }

    /**
     * Dizileri yeniler
     */
    private void refreshTvSeries() {
        tvSeriesList.clear();
        tvSeriesList.addAll(db.getAllTvSeries());
        tvSeriesAdapter.notifyDataSetChanged();
        
        // İlk açılışta sync işlemi yap
        if (!initialLoadDone) {
            syncTvSeriesWithRecommendationSystem(tvSeriesList);
            initialLoadDone = true;
        }
    }
    
    // Özel bir bayrak ekleyerek sadece etkinlik ilk oluşturulduğunda diziler sync edilsin
    private boolean initialLoadDone = false;
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshTvSeries();
    }
    
    /**
     * Dizileri öneri sistemine ekler
     * 
     * @param tvSeriesList Dizi listesi
     */
    private void syncTvSeriesWithRecommendationSystem(List<TvSeries> tvSeriesList) {
        if (tvSeriesList == null || tvSeriesList.isEmpty()) {
            return;
        }
        
        RecommendationService recommendationService = new RecommendationService(this);
        final int totalItems = tvSeriesList.size();
        final AtomicInteger processedItems = new AtomicInteger(0);
        
        // Her dizi için
        for (TvSeries tvSeries : tvSeriesList) {
            // Kullanıcının eklediği içerik olarak gönder
            Map<String, Object> content = new HashMap<>();
            content.put("item_id", tvSeries.getId());
            content.put("title", tvSeries.getName());
            content.put("content_type", "tv");  // Önemli: tv olarak işaretle
            content.put("overview", tvSeries.getDescription());
            content.put("poster_path", tvSeries.getPosterPath());
            
            // Kullanıcı ID'si varsayılan olarak 1
            recommendationService.addUserContent(1, content, new RecommendationService.ServiceCallback<StatusResponse>() {
                @Override
                public void onSuccess(StatusResponse response) {
                    Log.d(TAG, "Dizi öneri sistemine eklendi: " + tvSeries.getName());
                    
                    // Önemli: Dizi eklendikten hemen sonra değerlendirme ekle
                    float rating = 5.0f; // Varsayılan olarak en yüksek puan
                    
                    // Dizi değerlendirmesini ekle
                    recommendationService.addRating(1, tvSeries.getId(), rating, new RecommendationService.ServiceCallback<StatusResponse>() {
                        @Override
                        public void onSuccess(StatusResponse response) {
                            Log.d(TAG, "Dizi değerlendirmesi eklendi: " + tvSeries.getName() + " - " + rating + "/5");
                            
                            // Son dizi işlendiğinde, modeli eğit
                            if (processedItems.incrementAndGet() == totalItems) {
                                trainModelAfterDelay(recommendationService);
                            }
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Dizi değerlendirmesi eklenirken hata: " + errorMessage);
                            
                            // Hata olsa bile sayacı artır
                            if (processedItems.incrementAndGet() == totalItems) {
                                trainModelAfterDelay(recommendationService);
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Dizi öneri sistemine eklenirken hata: " + errorMessage);
                    
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
        // Tüm diziler eklendikten sonra 2 saniye bekle ve modeli eğit
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
                        Toast.makeText(TvSeriesActivity.this, "Öneriler güncellendi", Toast.LENGTH_SHORT).show();
                        
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
} 