package com.example.movieapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieapp.MovieDetailActivity;
import com.example.movieapp.R;
import com.example.movieapp.TvSeriesDetailActivity;
import com.example.movieapp.adapter.RecommendationAdapter;
import com.example.movieapp.api.RecommendationResponse;
import com.example.movieapp.decoration.SpacingItemDecoration;
import com.example.movieapp.service.RecommendationService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Öneri sistemi ekranı
 */
public class RecommendationFragment extends Fragment implements RecommendationAdapter.ItemClickListener {
    
    private static final String TAG = "RecommendationFragment";
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
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendations, container, false);
        
        // UI bileşenleri
        recyclerView = view.findViewById(R.id.recyclerViewRecommendations);
        progressBar = view.findViewById(R.id.progressBarRecommendations);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshRecommendations);
        emptyText = view.findViewById(R.id.textViewEmptyRecommendations);
        strategySpinner = view.findViewById(R.id.spinnerStrategy);
        contentTypeChipGroup = view.findViewById(R.id.chipGroupContentType);
        
        // RecyclerView yapılandırması
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.item_spacing)));
        
        // Adapter
        adapter = new RecommendationAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        
        // Servis
        recommendationService = new RecommendationService(getContext());
        
        // Strateji spinner'ı
        setupStrategySpinner();
        
        // İçerik tipi chip grubu
        setupContentTypeChips();
        
        // Yenileme işlemi
        swipeRefreshLayout.setOnRefreshListener(this::loadRecommendations);
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadRecommendations();
    }
    
    /**
     * Öneri stratejisi spinner'ını yapılandırır
     */
    private void setupStrategySpinner() {
        String[] strategies = {"Hibrit", "İşbirlikçi Filtreleme", "İçerik Tabanlı"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, strategies);
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
                        if (isAdded()) {
                            List<RecommendationResponse.RecommendedItem> recommendations = response.getRecommendations();
                            adapter.updateItems(recommendations);
                            
                            showProgress(false);
                            showEmptyText(recommendations.isEmpty());
                        }
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        if (isAdded()) {
                            showProgress(false);
                            showEmptyText(true);
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Öneri hatası: " + errorMessage);
                        }
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
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.putExtra("movie_id", item.getItemId());
            intent.putExtra("movie_title", item.getTitle());
            intent.putExtra("movie_poster", item.getFullPosterPath());
            intent.putExtra("movie_overview", item.getOverview());
            startActivity(intent);
        } else if (item.isTvShow()) {
            // Dizi detay ekranına git
            Intent intent = new Intent(getActivity(), TvSeriesDetailActivity.class);
            intent.putExtra("tv_id", item.getItemId());
            intent.putExtra("tv_title", item.getTitle());
            intent.putExtra("tv_poster", item.getFullPosterPath());
            intent.putExtra("tv_overview", item.getOverview());
            startActivity(intent);
        }
    }
} 