// TMDbApi.java

package com.example.movieapp.api;

import com.example.movieapp.api.MovieResponse;
import com.example.movieapp.api.VideoResponse;
import com.example.movieapp.api.TvResponse;
import com.example.movieapp.api.StatusResponse;
import com.example.movieapp.api.MarkFavoriteRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface TMDbApi {
    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(
            @Path("movie_id") int movieId,
            @Query("language") String language
    );

    @GET("search/tv")
    Call<TvResponse> searchTvSeries(
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult
    );

    @GET("tv/{tv_id}/videos")
    Call<VideoResponse> getTvVideos(
            @Path("tv_id") int tvId,
            @Query("language") String language
    );

    @POST("account/{account_id}/favorite")
    Call<StatusResponse> markAsFavorite(
        @Path("account_id") int accountId,
        @Query("session_id") String sessionId,
        @Body MarkFavoriteRequest request
    );

    @GET("account/{account_id}/favorite/movies")
    Call<MovieResponse> getFavoriteMovies(
        @Path("account_id") int accountId,
        @Query("session_id") String sessionId,
        @Query("language") String language,
        @Query("page") int page
    );

    @GET("account/{account_id}/favorite/tv")
    Call<TvResponse> getFavoriteTvShows(
        @Path("account_id") int accountId,
        @Query("session_id") String sessionId,
        @Query("language") String language,
        @Query("page") int page
    );
}
