package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieapp.adapter.RecommendationAdapter;
import com.example.movieapp.api.RecommendationResponse;
import com.example.movieapp.api.StatusResponse;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.decoration.SpacingItemDecoration;
import com.example.movieapp.service.RecommendationService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kişiselleştirilmiş öneri aktivitesi
 */
public class RecommendationActivity extends AppCompatActivity implements RecommendationAdapter.ItemClickListener {
    
    private static final String TAG = "RecommendationActivity";
    private static final int DEFAULT_USER_ID = 1; // Örnek kullanıcı ID
    private static final int DEFAULT_LIMIT = 20; // Varsayılan öneri limiti
    
    private RecyclerView recyclerView;
    private RecommendationAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyText;
    private Spinner strategySpinner;
    private ChipGroup contentTypeChipGroup;
    
    private RecommendationService recommendationService;
    private String currentStrategy = "hybrid";
    private String currentContentType = null; // null = tümü
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // UI bileşenleri
        recyclerView = findViewById(R.id.recyclerViewRecommendations);
        progressBar = findViewById(R.id.progressBarRecommendations);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshRecommendations);
        emptyText = findViewById(R.id.textViewEmptyRecommendations);
        strategySpinner = findViewById(R.id.spinnerStrategy);
        contentTypeChipGroup = findViewById(R.id.chipGroupContentType);
        
        // RecyclerView yapılandırması
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.item_spacing)));
        
        // Adapter
        adapter = new RecommendationAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Servis
        recommendationService = new RecommendationService(this);
        
        // Strateji spinner'ı
        setupStrategySpinner();
        
        // İçerik tipi chip grubu
        setupContentTypeChips();
        
        // Yenileme işlemi
        swipeRefreshLayout.setOnRefreshListener(this::loadRecommendations);
        
        // İlk yükleme
        loadRecommendations();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Öneri stratejisi spinner'ını yapılandırır
     */
    private void setupStrategySpinner() {
        String[] strategies = {"Hibrit", "İşbirlikçi Filtreleme", "İçerik Tabanlı"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, strategies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strategySpinner.setAdapter(spinnerAdapter);
        
        strategySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentStrategy = "hybrid";
                        break;
                    case 1:
                        currentStrategy = "collaborative";
                        break;
                    case 2:
                        currentStrategy = "content_based";
                        break;
                }
                loadRecommendations();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Bir şey seçilmedi
            }
        });
    }
    
    /**
     * İçerik tipi chip'lerini yapılandırır
     */
    private void setupContentTypeChips() {
        // Tümü chip'i
        Chip chipAll = (Chip) getLayoutInflater().inflate(R.layout.item_chip_choice, contentTypeChipGroup, false);
        chipAll.setText("Tümü");
        chipAll.setChecked(true);
        contentTypeChipGroup.addView(chipAll);
        
        // Film chip'i
        Chip chipMovies = (Chip) getLayoutInflater().inflate(R.layout.item_chip_choice, contentTypeChipGroup, false);
        chipMovies.setText("Filmler");
        contentTypeChipGroup.addView(chipMovies);
        
        // Dizi chip'i
        Chip chipTvShows = (Chip) getLayoutInflater().inflate(R.layout.item_chip_choice, contentTypeChipGroup, false);
        chipTvShows.setText("Diziler");
        contentTypeChipGroup.addView(chipTvShows);
        
        contentTypeChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }
            
            int selectedChipId = checkedIds.get(0);
            Chip selectedChip = group.findViewById(selectedChipId);
            
            if (selectedChip == chipAll) {
                currentContentType = null; // Tümü
            } else if (selectedChip == chipMovies) {
                currentContentType = "movie";
            } else if (selectedChip == chipTvShows) {
                currentContentType = "tv";
            }
            
            loadRecommendations();
        });
    }
    
    /**
     * Kullanıcı için önerileri yükler
     */
    private void loadRecommendations() {
        showProgress(true);
        showEmptyText(false);
        
        // Gerçek uygulamada kullanıcı kimliği oturum yöneticisinden alınabilir
        int userId = DEFAULT_USER_ID;
        
        recommendationService.getRecommendations(
                userId,
                DEFAULT_LIMIT,
                currentContentType,
                currentStrategy,
                new RecommendationService.ServiceCallback<RecommendationResponse>() {
                    @Override
                    public void onSuccess(RecommendationResponse response) {
                        List<RecommendationResponse.RecommendedItem> recommendations = response.getRecommendations();
                        adapter.updateItems(recommendations);
                        
                        showProgress(false);
                        showEmptyText(recommendations.isEmpty());
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        showProgress(false);
                        showEmptyText(true);
                        Toast.makeText(RecommendationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Öneri hatası: " + errorMessage);
                    }
                });
    }
    
    /**
     * İlerleme durumunu gösterir/gizler
     */
    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    
    /**
     * Boş mesajını gösterir/gizler
     */
    private void showEmptyText(boolean show) {
        if (emptyText != null) {
            emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    @Override
    public void onItemClick(RecommendationResponse.RecommendedItem item) {
        if (item.isMovie()) {
            // Film detay ekranına git
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("movie_id", item.getItemId());
            intent.putExtra("movie_title", item.getTitle());
            intent.putExtra("movie_poster", item.getFullPosterPath());
            intent.putExtra("movie_overview", item.getOverview());
            startActivity(intent);
        } else if (item.isTvShow()) {
            // Dizi detay ekranına git
            Intent intent = new Intent(this, TvSeriesDetailActivity.class);
            intent.putExtra("tv_id", item.getItemId());
            intent.putExtra("tv_title", item.getTitle());
            intent.putExtra("tv_poster", item.getFullPosterPath());
            intent.putExtra("tv_overview", item.getOverview());
            startActivity(intent);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Kullanıcı bu aktiviteye her döndüğünde, mevcut film ve dizi verilerini kullanarak öneri modelini güncelleyip yeni öneriler alacağız
        syncCurrentContentAndLoadRecommendations();
    }
    
    /**
     * Mevcut film ve dizi verileriyle öneri sistemini senkronize eder ve önerileri yükler
     */
    private void syncCurrentContentAndLoadRecommendations() {
        showProgress(true);
        showEmptyText(false);
        
        // Kullanıcı ID
        int userId = DEFAULT_USER_ID;
        
        // Veritabanından filmleri ve dizileri al
        DatabaseHelper db = new DatabaseHelper(this, userId);
        List<com.example.movieapp.model.Movie> movies = db.getAllMovies();
        List<com.example.movieapp.model.TvSeries> tvSeries = db.getAllTvSeries();
        
        // Senkronizasyon için sayaçlar
        final int totalItems = movies.size() + tvSeries.size();
        if (totalItems == 0) {
            // Hiç içerik yoksa doğrudan yükle
            loadRecommendations();
            return;
        }
        
        final AtomicInteger processedItems = new AtomicInteger(0);
        
        // Önce tüm filmleri sync et
        for (com.example.movieapp.model.Movie movie : movies) {
            // Kullanıcının eklediği içerik olarak gönder
            Map<String, Object> content = new HashMap<>();
            content.put("item_id", movie.getId());
            content.put("title", movie.getTitle());
            content.put("content_type", "movie");
            content.put("overview", movie.getDescription());
            content.put("poster_path", movie.getPosterPath());
            
            // Kullanıcı içeriğini ekle
            recommendationService.addUserContent(userId, content, new RecommendationService.ServiceCallback<StatusResponse>() {
                @Override
                public void onSuccess(StatusResponse response) {
                    Log.d(TAG, "Film öneri sistemine eklendi: " + movie.getTitle());
                    
                    // Film için değerlendirme ekle
                    recommendationService.addRating(userId, movie.getId(), movie.getRating(), new RecommendationService.ServiceCallback<StatusResponse>() {
                        @Override
                        public void onSuccess(StatusResponse response) {
                            Log.d(TAG, "Film değerlendirmesi eklendi: " + movie.getTitle() + " - " + movie.getRating() + "/10");
                            
                            checkAndProceed(processedItems, totalItems);
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Film değerlendirmesi eklenirken hata: " + errorMessage);
                            checkAndProceed(processedItems, totalItems);
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Film öneri sistemine eklenirken hata: " + errorMessage);
                    checkAndProceed(processedItems, totalItems);
                }
            });
        }
        
        // Sonra tüm dizileri sync et
        for (com.example.movieapp.model.TvSeries series : tvSeries) {
            // Kullanıcının eklediği içerik olarak gönder
            Map<String, Object> content = new HashMap<>();
            content.put("item_id", series.getId());
            content.put("title", series.getName());
            content.put("content_type", "tv");
            content.put("overview", series.getDescription());
            content.put("poster_path", series.getPosterPath());
            
            // Kullanıcı içeriğini ekle
            recommendationService.addUserContent(userId, content, new RecommendationService.ServiceCallback<StatusResponse>() {
                @Override
                public void onSuccess(StatusResponse response) {
                    Log.d(TAG, "Dizi öneri sistemine eklendi: " + series.getName());
                    
                    // Dizi için değerlendirme ekle
                    recommendationService.addRating(userId, series.getId(), series.getRating(), new RecommendationService.ServiceCallback<StatusResponse>() {
                        @Override
                        public void onSuccess(StatusResponse response) {
                            Log.d(TAG, "Dizi değerlendirmesi eklendi: " + series.getName() + " - " + series.getRating() + "/10");
                            
                            checkAndProceed(processedItems, totalItems);
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Dizi değerlendirmesi eklenirken hata: " + errorMessage);
                            checkAndProceed(processedItems, totalItems);
                        }
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Dizi öneri sistemine eklenirken hata: " + errorMessage);
                    checkAndProceed(processedItems, totalItems);
                }
            });
        }
    }
    
    /**
     * İşlenen öğe sayısını kontrol eder ve tamamlandığında modeli eğitip önerileri yükler
     */
    private void checkAndProceed(AtomicInteger processedItems, int totalItems) {
        if (processedItems.incrementAndGet() == totalItems) {
            // Tüm içerikler sync edildikten sonra modeli eğit
            trainModelAndLoadRecommendations();
        }
    }
    
    /**
     * Öneri modelini eğitir ve önerileri yükler
     */
    private void trainModelAndLoadRecommendations() {
        // Kullanıcı ID
        int userId = DEFAULT_USER_ID;
        
        // Modeli eğit
        recommendationService.trainUserModel(userId, new RecommendationService.ServiceCallback<StatusResponse>() {
            @Override
            public void onSuccess(StatusResponse response) {
                Log.d(TAG, "Öneri modeli başarıyla eğitildi");
                
                // Önerileri yükle
                loadRecommendations();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Öneri modeli eğitilirken hata: " + errorMessage);
                
                // Hata olsa da önerileri yüklemeyi dene
                loadRecommendations();
            }
        });
    }
} 