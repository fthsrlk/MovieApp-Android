package com.example.movieapp.decoration;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

public class DynamicItemAnimator extends RecyclerView.ItemDecoration {
    private static final float MAX_SCALE = 1.15f;
    private static final float MIN_SCALE = 0.9f;
    private static final float MAX_ELEVATION = 32f;
    private static final float MIN_ELEVATION = 1f;

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        float viewportHeight = parent.getHeight();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof MaterialCardView) {
                MaterialCardView card = (MaterialCardView) child;
                
                // Yukarıdan uzaklığı hesapla (0 = en üst, 1 = en alt)
                float topPosition = child.getTop();
                float distanceFromTop = Math.max(0, Math.min(topPosition / viewportHeight, 1));
                
                // Ölçek hesapla (yukarı = büyük, aşağı = küçük)
                float scale = MAX_SCALE - (distanceFromTop * (MAX_SCALE - MIN_SCALE));
                
                // Elevation hesapla (yukarı = yüksek, aşağı = alçak)
                float elevation = MAX_ELEVATION - (distanceFromTop * (MAX_ELEVATION - MIN_ELEVATION));

                // Yatay kaydırma efekti (merkeze göre)
                float centerY = parent.getHeight() / 2f;
                float childCenterY = child.getTop() + child.getHeight() / 2f;
                float distanceFromCenter = (childCenterY - centerY) / viewportHeight;
                float xOffset = 100f * distanceFromCenter;

                // Animasyonlu uygula
                child.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .translationX(xOffset)
                    .translationZ(elevation)
                    .setDuration(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

                // Kart elevation'ını ayarla
                card.setCardElevation(elevation);
                
                // Rotasyon efekti (merkeze göre)
                float rotation = -10f * distanceFromCenter;
                child.setRotationX(rotation);
            }
        }
    }

    @Override
    public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // Kartlar arası boşluk
        outRect.top = 24;
        outRect.bottom = 24;
        outRect.left = 16;
        outRect.right = 16;
    }
} 