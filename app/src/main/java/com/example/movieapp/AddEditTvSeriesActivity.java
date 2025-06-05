package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TvResponse;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.TvSeries;
import com.example.movieapp.adapter.TvSeriesAdapter;
import com.example.movieapp.api.MarkFavoriteRequest;
import com.example.movieapp.api.StatusResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddEditTvSeriesActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private DatabaseHelper db;
    private TMDbApi apiService;
    private RecyclerView searchResultsRecyclerView;
    private TvSeriesAdapter searchResultsAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_tv_series);

        // Hesap ID'sini al
        int accountId = getSharedPreferences("auth", MODE_PRIVATE).getInt("account_id", -1);
        if (accountId == -1) {
            // Hesap ID'si yoksa login ekranına yönlendir
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        db = new DatabaseHelper(this, accountId);
        apiService = ApiClient.getClient().create(TMDbApi.class);

        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        searchTvSeries(query);
                    } else {
                        if (searchResultsAdapter != null) {
                            searchResultsAdapter = new TvSeriesAdapter(AddEditTvSeriesActivity.this, 
                                new ArrayList<>(), null);
                            searchResultsRecyclerView.setAdapter(searchResultsAdapter);
                        }
                    }
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Otomatik fokus ve klavye gösterme
        editTextTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextTitle, InputMethodManager.SHOW_IMPLICIT);
    }

    private void searchTvSeries(String title) {
        Call<TvResponse> call = apiService.searchTvSeries(
                title,
                "tr-TR",
                1,
                false
        );

        call.enqueue(new Callback<TvResponse>() {
            @Override
            public void onResponse(Call<TvResponse> call, Response<TvResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TvResponse tvResponse = response.body();
                    List<TvSeries> results = tvResponse.getResults();
                    if (results != null && !results.isEmpty()) {
                        searchResultsAdapter = new TvSeriesAdapter(AddEditTvSeriesActivity.this, results, new TvSeriesAdapter.OnTvSeriesClickListener() {
                            @Override
                            public void onTvSeriesClick(TvSeries tvSeries) {
                                addTvSeriesToFavorites(tvSeries);
                            }

                            @Override
                            public void onWatchClick(TvSeries tvSeries) {
                                // Arama sonuçlarında bu butona gerek yok
                            }

                            @Override
                            public void onEditClick(TvSeries tvSeries) {
                                // Arama sonuçlarında bu butona gerek yok
                            }
                        });
                        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
                    } else {
                        Toast.makeText(AddEditTvSeriesActivity.this, "Dizi bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AddEditTvSeriesActivity.this, "Hata: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddEditTvSeriesActivity.this, "Hata: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TvResponse> call, Throwable t) {
                Toast.makeText(AddEditTvSeriesActivity.this, "API çağrısı başarısız: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void addTvSeriesToFavorites(TvSeries tvSeries) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String sessionId = prefs.getString("session_id", "");
        int accountId = prefs.getInt("account_id", -1);
        boolean isGuest = prefs.getBoolean("is_guest", false);

        // Misafir girişi için direkt veritabanına ekle
        if (isGuest) {
            db.addTvSeries(tvSeries);
            Toast.makeText(AddEditTvSeriesActivity.this, "Dizi eklendi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Normal kullanıcı için TMDB senkronizasyonu
        if (sessionId.isEmpty() || accountId == -1) {
            Toast.makeText(this, "Oturum hatası", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkFavoriteRequest request = new MarkFavoriteRequest("tv", tvSeries.getTmdbId(), true);
        
        apiService.markAsFavorite(accountId, sessionId, request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatusCode() == 1 || 
                        response.body().getStatusCode() == 12) {
                        db.addTvSeries(tvSeries);
                        Toast.makeText(AddEditTvSeriesActivity.this, "Dizi favorilere eklendi", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditTvSeriesActivity.this, 
                            "Hata: " + response.body().getStatusMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEditTvSeriesActivity.this, "Dizi eklenemedi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(AddEditTvSeriesActivity.this, "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 