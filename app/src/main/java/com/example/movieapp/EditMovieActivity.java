// File: com/example/movieapp/EditMovieActivity.java

package com.example.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.Movie;

import androidx.appcompat.app.AppCompatActivity;

public class EditMovieActivity extends AppCompatActivity {

    private EditText editTextRating, editTextReview;
    private Button buttonSave;
    private DatabaseHelper db;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

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

        int movieId = getIntent().getIntExtra("movieId", -1);
        if (movieId != -1) {
            movie = db.getMovie(movieId);

            if (movie != null) {
                editTextRating.setText(String.valueOf(movie.getRating()));
                editTextReview.setText(movie.getReview());
            }
        }

        buttonSave.setOnClickListener(view -> {
            String ratingStr = editTextRating.getText().toString();
            if (!ratingStr.isEmpty()) {
                float rating = Float.parseFloat(ratingStr);
                String review = editTextReview.getText().toString();

                movie.setRating(rating);
                movie.setReview(review);

                db.updateMovie(movie);
                Toast.makeText(this, "Film güncellendi", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Puanı girin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
