// AddEditMovieActivity.java

package com.example.movieapp;

import android.os.Bundle;
import android.widget.Button;
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
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.MovieResponse;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.Movie;
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
import com.example.movieapp.adapter.MovieAdapter;

public class AddEditMovieActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private DatabaseHelper db;
    private TMDbApi apiService;
    private RecyclerView searchResultsRecyclerView;
    private MovieAdapter searchResultsAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500; // 500ms gecikme

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_movie);

        // Hesap ID'sini al
        int accountId = getSharedPreferences("auth", MODE_PRIVATE).getInt("account_id", -1);
        if (accountId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        db = new DatabaseHelper(this, accountId);
        apiService = ApiClient.getClient().create(TMDbApi.class);

        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Otomatik fokus ve klavye gösterme
        editTextTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextTitle, InputMethodManager.SHOW_IMPLICIT);

        // TextWatcher ekle
        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Önceki aramayı iptal et
                searchHandler.removeCallbacks(searchRunnable);

                // Yeni arama için gecikme ekle
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        searchMovie(query);
                    } else {
                        // Arama boşsa sonuçları temizle
                        if (searchResultsAdapter != null) {
                            searchResultsAdapter = new MovieAdapter(AddEditMovieActivity.this, 
                                new ArrayList<>(), null);
                            searchResultsRecyclerView.setAdapter(searchResultsAdapter);
                        }
                    }
                };

                // 500ms sonra aramayı başlat
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Klavyeyi kapat
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    private void searchMovie(String title) {
        Call<MovieResponse> call = apiService.searchMovies(
                title,
                "tr-TR",
                1,
                false
        );

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    List<Movie> results = movieResponse.getResults();
                    if (results != null && !results.isEmpty()) {
                        searchResultsAdapter = new MovieAdapter(AddEditMovieActivity.this, results, new MovieAdapter.OnMovieClickListener() {
                            @Override
                            public void onMovieClick(Movie movie) {
                                // Önce TMDB'ye favorilere ekle
                                addMovieToFavorites(movie);
                            }

                            @Override
                            public void onWatchClick(Movie movie) {
                                // Arama sonuçlarında bu butona gerek yok
                            }

                            @Override
                            public void onEditClick(Movie movie) {
                                // Arama sonuçlarında bu butona gerek yok
                            }
                        });
                        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
                    } else {
                        Toast.makeText(AddEditMovieActivity.this, "Film bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(AddEditMovieActivity.this, "Hata: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddEditMovieActivity.this, "Hata: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(AddEditMovieActivity.this, "API çağrısı başarısız: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void addMovieToFavorites(Movie movie) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String sessionId = prefs.getString("session_id", "");
        int accountId = prefs.getInt("account_id", -1);
        boolean isGuest = prefs.getBoolean("is_guest", false);

        // Misafir girişi için direkt veritabanına ekle
        if (isGuest) {
            db.addMovie(movie);
            Toast.makeText(AddEditMovieActivity.this, "Film eklendi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Normal kullanıcı için TMDB senkronizasyonu
        if (sessionId.isEmpty() || accountId == -1) {
            Toast.makeText(this, "Oturum hatası", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkFavoriteRequest request = new MarkFavoriteRequest("movie", movie.getTmdbId(), true);
        
        apiService.markAsFavorite(accountId, sessionId, request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatusCode() == 1 || 
                        response.body().getStatusCode() == 12) {
                        db.addMovie(movie);
                        Toast.makeText(AddEditMovieActivity.this, "Film favorilere eklendi", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditMovieActivity.this, 
                            "Hata: " + response.body().getStatusMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEditMovieActivity.this, "Film eklenemedi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(AddEditMovieActivity.this, "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
