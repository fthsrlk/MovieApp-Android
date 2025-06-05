package com.example.movieapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.R;
import com.example.movieapp.api.RecommendationResponse;
import com.google.android.material.chip.Chip;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Öneri listesi için RecyclerView adapter'ı
 */
public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    
    private final Context context;
    private List<RecommendationResponse.RecommendedItem> items;
    private final ItemClickListener clickListener;
    private final DecimalFormat scoreFormat = new DecimalFormat("#.##");
    
    public RecommendationAdapter(Context context, List<RecommendationResponse.RecommendedItem> items, ItemClickListener clickListener) {
        this.context = context;
        this.items = items;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendationResponse.RecommendedItem item = items.get(position);
        
        try {
            // Başlık
            String title = item.getTitle();
            holder.title.setText(title != null && !title.isEmpty() ? title : context.getString(R.string.unknown_title));
            
            // Skor
            float score = item.getScore();
            holder.score.setText(scoreFormat.format(score));
            
            // İçerik tipi chip
            if (item.isMovie()) {
                holder.contentType.setText(R.string.content_type_movie);
            } else if (item.isTvShow()) {
                holder.contentType.setText(R.string.content_type_tv);
            } else {
                String contentType = item.getContentType();
                holder.contentType.setText(contentType != null && !contentType.isEmpty() ? 
                        contentType : context.getString(R.string.unknown_type));
            }
            
            // Poster resmi
            String posterUrl = item.getFullPosterPath();
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(context)
                        .load(posterUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                        .placeholder(R.drawable.placeholder_poster)
                        .error(R.drawable.placeholder_poster)
                        .into(holder.poster);
            } else {
                holder.poster.setImageResource(R.drawable.placeholder_poster);
            }
            
            // Özet
            String overview = item.getOverview();
            if (overview != null && !overview.isEmpty() && 
                !overview.equals("null") && !overview.equals("NaN")) {
                holder.overview.setText(overview);
                holder.overview.setVisibility(View.VISIBLE);
            } else {
                holder.overview.setVisibility(View.GONE);
            }
            
            // Tıklama olayı
            holder.cardView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(item);
                }
            });
        } catch (Exception e) {
            // Hata durumunda loglama yap ve mümkün olduğunca devam etmeye çalış
            Log.e("RecommendationAdapter", "Öneri görüntüleme hatası: " + e.getMessage());
            
            // Temel bilgileri göster
            holder.title.setText(String.format(context.getString(R.string.item_id_format), item.getItemId()));
            holder.overview.setVisibility(View.GONE);
            holder.poster.setImageResource(R.drawable.placeholder_poster);
            holder.contentType.setText(R.string.unknown_type);
        }
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Öğeleri günceller
     * 
     * @param newItems Yeni öğe listesi
     */
    public void updateItems(List<RecommendationResponse.RecommendedItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder sınıfı
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final ImageView poster;
        final TextView title;
        final TextView overview;
        final TextView score;
        final Chip contentType;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewRecommendation);
            poster = itemView.findViewById(R.id.imageViewRecommendationPoster);
            title = itemView.findViewById(R.id.textViewRecommendationTitle);
            overview = itemView.findViewById(R.id.textViewRecommendationOverview);
            score = itemView.findViewById(R.id.textViewRecommendationScore);
            contentType = itemView.findViewById(R.id.chipRecommendationContentType);
        }
    }
    
    /**
     * Öğe tıklama olayı dinleyici arayüzü
     */
    public interface ItemClickListener {
        void onItemClick(RecommendationResponse.RecommendedItem item);
    }
} 