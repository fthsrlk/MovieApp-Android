// File: com/example/movieapp/MovieDetailActivity.java

package com.example.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.movieapp.database.DatabaseHelper;
import com.example.movieapp.model.Movie;
import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.api.VideoResponse;
import com.example.movieapp.api.StatusResponse;
import com.example.movieapp.api.MarkFavoriteRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.appbar.MaterialToolbar;
import android.view.View;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import androidx.cardview.widget.CardView;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetailPoster;
    private TextView textViewDetailTitle, textViewDetailYear, textViewDetailRating, textViewDetailDescription;
    private RatingBar ratingBarDetail;
    private MaterialButton buttonEdit, buttonDelete, buttonWatchVideo;
    private DatabaseHelper db;
    private Movie movie;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);

        int accountId = getSharedPreferences("auth", MODE_PRIVATE).getInt("account_id", -1);
        if (accountId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = new DatabaseHelper(this, accountId);

        // Views
        imageViewDetailPoster = findViewById(R.id.imageViewDetailPoster);
        textViewDetailTitle = findViewById(R.id.textViewDetailTitle);
        textViewDetailYear = findViewById(R.id.textViewDetailYear);
        textViewDetailRating = findViewById(R.id.textViewDetailRating);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        ratingBarDetail = findViewById(R.id.ratingBarDetail);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonWatchVideo = findViewById(R.id.buttonWatchVideo);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar ayarları
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Intent'ten film ID'sini al - hem "movieId" hem de "movie_id" parametrelerini kontrol et
        int movieId = getIntent().getIntExtra("movieId", -1);
        if (movieId == -1) {
            // "movie_id" parametresini kontrol et (MainActivity'den gönderilen)
            movieId = getIntent().getIntExtra("movie_id", -1);
        }
        
        if (movieId != -1) {
            movie = db.getMovie(movieId);

            if (movie != null) {
                // Başlık
                toolbar.setTitle(movie.getTitle());
                textViewDetailTitle.setText(movie.getTitle());
                
                // Yıl
                textViewDetailYear.setText(movie.getYear());
                
                // Puan (5 üzerinden)
                float rating = movie.getRating() / 2;
                ratingBarDetail.setRating(rating);
                textViewDetailRating.setText(String.format("%.1f/5 ★", rating));
                
                // Açıklama
                textViewDetailDescription.setText(movie.getDescription());

                // Poster
                Glide.with(this)
                        .load(movie.getImgUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                Palette.from(bitmap).generate(palette -> {
                                    // Renkleri al
                                    int defaultColor = Color.WHITE;
                                    int dominantColor = palette.getDominantColor(defaultColor);
                                    int mutedColor = palette.getMutedColor(defaultColor);
                                    int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
                                    int darkMutedColor = palette.getDarkMutedColor(Color.DKGRAY);
                                    
                                    // Daha koyu renkler oluştur
                                    int veryDarkMutedColor = adjustBrightness(darkMutedColor, 0.7f); // %70 daha koyu
                                    int veryDarkVibrantColor = adjustBrightness(darkVibrantColor, 0.7f); // %70 daha koyu

                                    // Gradient oluştur
                                    GradientDrawable gradient = new GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        new int[]{Color.TRANSPARENT, adjustAlpha(dominantColor, 0.6f), adjustAlpha(mutedColor, 0.9f)}
                                    );

                                    // Gradient'i uygula
                                    View gradientView = findViewById(R.id.gradientOverlay);
                                    gradientView.setBackground(gradient);

                                    // CardView'ı bul ve renklerini ayarla
                                    CardView contentCard = findViewById(R.id.contentCard);
                                    contentCard.setCardBackgroundColor(adjustAlpha(mutedColor, 0.95f));

                                    // Arka plan rengi için daha koyu bir ton oluştur
                                    int backgroundColor = adjustAlpha(mutedColor, 0.98f);
                                    NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
                                    nestedScrollView.setBackgroundColor(backgroundColor);

                                    // UI elementlerinin renklerini ayarla
                                    textViewDetailTitle.setTextColor(veryDarkVibrantColor);
                                    textViewDetailYear.setTextColor(veryDarkMutedColor);
                                    ratingBarDetail.setProgressTintList(ColorStateList.valueOf(veryDarkVibrantColor));
                                    textViewDetailRating.setTextColor(veryDarkMutedColor);
                                    textViewDetailDescription.setTextColor(veryDarkVibrantColor);
                                    
                                    // Butonları ayarla
                                    buttonWatchVideo.setBackgroundTintList(ColorStateList.valueOf(veryDarkVibrantColor));
                                    buttonEdit.setBackgroundTintList(ColorStateList.valueOf(veryDarkVibrantColor));
                                    buttonDelete.setStrokeColor(ColorStateList.valueOf(veryDarkMutedColor));
                                    buttonDelete.setTextColor(veryDarkMutedColor);

                                    // Toolbar rengini ayarla
                                    toolbar.setTitleTextColor(veryDarkVibrantColor);
                                    toolbar.setNavigationIconTint(veryDarkVibrantColor);
                                });
                                return false;
                            }
                        })
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageViewDetailPoster);
            }
        }

        buttonEdit.setOnClickListener(view -> {
            Intent intent = new Intent(MovieDetailActivity.this, EditMovieActivity.class);
            intent.putExtra("movieId", movie.getId());
            startActivity(intent);
        });

        buttonDelete.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                .setTitle("Filmi Sil")
                .setMessage("Bu filmi silmek istediğinizden emin misiniz?")
                .setNegativeButton("İptal", null)
                .setPositiveButton("Sil", (dialog, which) -> {
                    deleteMovie();
                })
                .show();
        });

        buttonWatchVideo.setOnClickListener(view -> {
            TMDbApi tmdbApi = ApiClient.getClient().create(TMDbApi.class);
            Call<VideoResponse> call = tmdbApi.getMovieVideos(movie.getTmdbId(), "tr-TR");
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
                            } else {
                                showError("Video bulunamadı");
                            }
                        } else {
                            // Türkçe video bulunamadıysa İngilizce deneyelim
                            searchVideo("en-US");
                        }
                    } else {
                        showError("Video yüklenemedi");
                    }
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    showError("Bağlantı hatası: " + t.getMessage());
                }
            });
        });
    }

    private void searchVideo(String language) {
        TMDbApi tmdbApi = ApiClient.getClient().create(TMDbApi.class);
        Call<VideoResponse> call = tmdbApi.getMovieVideos(movie.getTmdbId(), language);
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
                        } else {
                            showError("Video bulunamadı");
                        }
                    } else {
                        showError("Video bulunamadı");
                    }
                } else {
                    showError("Video yüklenemedi");
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                showError("Bağlantı hatası: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movie != null) {
            movie = db.getMovie(movie.getId());
            if (movie != null) {
                float rating = movie.getRating() / 2;
                ratingBarDetail.setRating(rating);
                textViewDetailRating.setText(String.format("%.1f/5 ★", rating));
            }
        }
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    // Renk koyulaştırma yardımcı metodu
    private int adjustBrightness(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // Parlaklığı azalt
        return Color.HSVToColor(Color.alpha(color), hsv);
    }

    private void deleteMovie() {
        // Film nesnesinin null kontrolü ekle
        if (movie == null) {
            Toast.makeText(MovieDetailActivity.this, "Film bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String sessionId = prefs.getString("session_id", "");
        int accountId = prefs.getInt("account_id", -1);
        boolean isGuest = prefs.getBoolean("is_guest", false);

        // Misafir girişi için direkt veritabanından sil
        if (isGuest) {
            db.deleteMovie(movie.getId());
            Toast.makeText(MovieDetailActivity.this, "Film silindi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Normal kullanıcı için TMDB senkronizasyonu
        if (sessionId.isEmpty() || accountId == -1) {
            Toast.makeText(this, "Oturum hatası", Toast.LENGTH_SHORT).show();
            return;
        }

        MarkFavoriteRequest request = new MarkFavoriteRequest("movie", movie.getTmdbId(), false);
        
        TMDbApi apiService = ApiClient.getClient().create(TMDbApi.class);
        apiService.markAsFavorite(accountId, sessionId, request).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    db.deleteMovie(movie.getId());
                    Toast.makeText(MovieDetailActivity.this, "Film silindi", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MovieDetailActivity.this, "Film silinemedi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, "Bağlantı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
