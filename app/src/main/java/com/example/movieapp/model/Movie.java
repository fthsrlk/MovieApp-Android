// File: com/example/movieapp/model/Movie.java

package com.example.movieapp.model;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("localId")
    private int id; // Database primary key

    @SerializedName("id")
    private int tmdbId; // TMDb movie ID

    private String title;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("overview")
    private String description;

    @SerializedName("vote_average")
    private float rating;

    @SerializedName("poster_path")
    private String posterPath;

    private String review; // User's review

    // Added fields
    private String year;    // Movie year
    private int ranking;    // User's ranking
    private String imgUrl;  // Image URL

    // Getter and Setter methods

    public int getId() {
        return id;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title != null ? title : "N/A";
    }

    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : "N/A";
    }

    public String getDescription() {
        return description != null ? description : "No description";
    }

    public float getRating() {
        return rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReview() {
        return review != null ? review : "";
    }

    public String getYear() {
        if (year != null && !year.isEmpty()) {
            return year;
        } else if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        } else {
            return "N/A";
        }
    }

    public int getRanking() {
        return ranking;
    }

    public String getImgUrl() {
        if (imgUrl != null && !imgUrl.isEmpty()) {
            return imgUrl;
        } else if (posterPath != null) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        } else {
            return null;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        // Automatically set the year when releaseDate is set
        if (releaseDate != null && releaseDate.length() >= 4) {
            this.year = releaseDate.substring(0, 4);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        // Automatically set the imgUrl when posterPath is set
        if (posterPath != null) {
            this.imgUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
        }
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
