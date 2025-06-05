// MovieViewHolder.java

package com.example.movieapp.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageViewPoster;
    public TextView textViewTitle;
    public TextView textViewYear;

    public MovieViewHolder(View itemView) {
        super(itemView);
        imageViewPoster = itemView.findViewById(R.id.movie_image);
        textViewTitle = itemView.findViewById(R.id.movie_title);
        textViewYear = itemView.findViewById(R.id.textViewYear);
    }
}
