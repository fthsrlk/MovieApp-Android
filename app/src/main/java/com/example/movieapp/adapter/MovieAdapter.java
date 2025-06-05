// MovieAdapter.java

package com.example.movieapp.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RatingBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.movieapp.MovieDetailActivity;
import com.example.movieapp.R;
import com.example.movieapp.api.ApiClient;
import com.example.movieapp.api.TMDbApi;
import com.example.movieapp.api.VideoResponse;
import com.example.movieapp.model.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movies;
    private OnMovieClickListener onMovieClickListener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
        void onWatchClick(Movie movie);
        void onEditClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movies, OnMovieClickListener listener) {
        this.context = context;
        this.movies = movies;
        this.onMovieClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewYear.setText(movie.getYear());
        
        // Puanı 5 üzerinden göster
        float rating = movie.getRating() / 2;
        holder.ratingBar.setRating(rating);
        holder.textViewRating.setText(String.format("%.1f/5", rating));

        // Poster resmini yükle ve renkleri ayarla
        holder.imageViewPoster.setTag(movie.getId());
        Glide.with(context)
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
                            int vibrantColor = palette.getVibrantColor(Color.BLACK);
                            int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
                            int darkMutedColor = palette.getDarkMutedColor(Color.DKGRAY);
                            int lightVibrantColor = palette.getLightVibrantColor(Color.WHITE);
                            
                            // Daha koyu renkler oluştur
                            int veryDarkMutedColor = adjustBrightness(darkMutedColor, 0.7f);
                            int veryDarkVibrantColor = adjustBrightness(darkVibrantColor, 0.7f);

                            // MaterialCardView arka planını ayarla
                            MaterialCardView cardView = (MaterialCardView) holder.itemView;
                            cardView.setCardBackgroundColor(adjustAlpha(mutedColor, 0.95f));
                            cardView.setStrokeColor(adjustAlpha(darkVibrantColor, 0.1f));
                            
                            // Metin renklerini ayarla
                            holder.textViewTitle.setTextColor(veryDarkVibrantColor);
                            holder.textViewYear.setTextColor(veryDarkMutedColor);
                            holder.ratingBar.setProgressTintList(ColorStateList.valueOf(darkVibrantColor));
                            holder.textViewRating.setTextColor(veryDarkMutedColor);

                            // Butonlar için ortak arka plan ve metin rengi oluştur
                            int buttonBgColor = adjustAlpha(vibrantColor, 0.12f);  // Hafif transparan canlı renk
                            int buttonTextColor = darkVibrantColor;  // Koyu canlı renk

                            // İzle ve Düzenle butonları için aynı renkleri kullan
                            ColorStateList buttonBgColorState = ColorStateList.valueOf(buttonBgColor);
                            ColorStateList buttonTextColorState = ColorStateList.valueOf(buttonTextColor);
                            ColorStateList rippleColorState = ColorStateList.valueOf(adjustAlpha(vibrantColor, 0.2f));

                            // İzle butonu
                            holder.chipWatch.setChipBackgroundColor(buttonBgColorState);
                            holder.chipWatch.setTextColor(buttonTextColor);
                            holder.chipWatch.setChipIconTint(buttonTextColorState);
                            holder.chipWatch.setRippleColor(rippleColorState);

                            // Düzenle butonu
                            holder.chipEdit.setChipBackgroundColor(buttonBgColorState);
                            holder.chipEdit.setTextColor(buttonTextColor);
                            holder.chipEdit.setChipIconTint(buttonTextColorState);
                            holder.chipEdit.setRippleColor(rippleColorState);
                        });
                        return false;
                    }
                })
                .placeholder(R.drawable.ic_movie_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageViewPoster);

        // Tıklama olayları
        holder.itemView.setOnClickListener(v -> {
            if (onMovieClickListener != null) {
                onMovieClickListener.onMovieClick(movie);
            }
        });

        holder.chipWatch.setOnClickListener(v -> {
            if (onMovieClickListener != null) {
                onMovieClickListener.onWatchClick(movie);
            }
        });

        holder.chipEdit.setOnClickListener(v -> {
            if (onMovieClickListener != null) {
                onMovieClickListener.onEditClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewYear;
        RatingBar ratingBar;
        TextView textViewRating;
        Chip chipWatch;
        Chip chipEdit;

        MovieViewHolder(View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            chipWatch = itemView.findViewById(R.id.chipWatch);
            chipEdit = itemView.findViewById(R.id.chipEdit);
        }
    }

    // Yardımcı metodlar
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private int adjustBrightness(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(Color.alpha(color), hsv);
    }
}
