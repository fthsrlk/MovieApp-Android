package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import android.app.ActivityOptions;
import android.widget.Button;
import com.example.movieapp.ui.RecommendationFragment;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Oturum kontrolü
        if (!isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main_menu);

        // Toolbar'ı ayarla
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MaterialCardView moviesCard = findViewById(R.id.moviesCard);
        MaterialCardView tvSeriesCard = findViewById(R.id.tvSeriesCard);

        moviesCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        });

        tvSeriesCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, TvSeriesActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        });

        // Öneri ekranı butonunu ekle
        Button btnRecommendations = findViewById(R.id.btnRecommendations);
        btnRecommendations.setOnClickListener(v -> {
            // Öneri aktivitesine git
            Intent intent = new Intent(MainMenuActivity.this, RecommendationActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        return prefs.contains("session_id");
    }

    private void logout() {
        // Oturum bilgilerini temizle
        getSharedPreferences("auth", MODE_PRIVATE)
            .edit()
            .clear()
            .apply();

        // Login ekranına yönlendir
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int getAccountId() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        return prefs.getInt("account_id", -1);
    }
} 