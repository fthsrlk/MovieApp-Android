package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.TvSeries;

import androidx.appcompat.app.AppCompatActivity;

public class EditTvSeriesActivity extends AppCompatActivity {

    private EditText editTextRating, editTextReview;
    private Button buttonSave;
    private DatabaseHelper db;
    private TvSeries tvSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tv_series);

        int accountId = getSharedPreferences("auth", MODE_PRIVATE).getInt("account_id", -1);
        if (accountId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = new DatabaseHelper(this, accountId);

        editTextRating = findViewById(R.id.editTextRating);
        editTextReview = findViewById(R.id.editTextReview);
        buttonSave = findViewById(R.id.buttonSave);

        int tvSeriesId = getIntent().getIntExtra("tvSeriesId", -1);
        if (tvSeriesId != -1) {
            tvSeries = db.getTvSeries(tvSeriesId);

            if (tvSeries != null) {
                editTextRating.setText(String.valueOf(tvSeries.getRating()));
                editTextReview.setText(tvSeries.getReview());
            }
        }

        buttonSave.setOnClickListener(view -> {
            String ratingStr = editTextRating.getText().toString();
            if (!ratingStr.isEmpty()) {
                float rating = Float.parseFloat(ratingStr);
                String review = editTextReview.getText().toString();

                tvSeries.setRating(rating);
                tvSeries.setReview(review);

                db.updateTvSeries(tvSeries);
                Toast.makeText(this, "Dizi güncellendi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Puanı girin", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 