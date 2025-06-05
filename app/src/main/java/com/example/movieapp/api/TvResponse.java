package com.example.movieapp.api;

import com.example.movieapp.model.TvSeries;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TvResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<TvSeries> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public List<TvSeries> getResults() {
        return results;
    }

    public void setResults(List<TvSeries> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
} 