package com.example.movieapp.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView öğeleri arasında boşluk bırakmak için ItemDecoration
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    
    private final int spacing;
    
    /**
     * Öğeler arasında boşluk bırakmak için dekorasyon oluşturur
     * 
     * @param spacing Boşluk miktarı (piksel)
     */
    public SpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }
    
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        
        // İlk öğe için üst boşluk yok
        if (position == 0) {
            outRect.top = 0;
        } else {
            outRect.top = spacing;
        }
        
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;
    }
} 