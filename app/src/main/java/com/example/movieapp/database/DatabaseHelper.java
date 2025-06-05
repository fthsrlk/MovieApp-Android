// File: com/example/movieapp/database/DatabaseHelper.java

package com.example.movieapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.movieapp.model.Movie;
import com.example.movieapp.model.TvSeries;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 5;

    // Movie table
    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TMDB_ID = "tmdb_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_RANKING = "ranking";
    public static final String COLUMN_REVIEW = "review";
    public static final String COLUMN_IMG_URL = "imgUrl";
    public static final String COLUMN_ACCOUNT_ID = "account_id";

    // TV Series table
    public static final String TABLE_TV_SERIES = "tv_series";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FIRST_AIR_DATE = "first_air_date";

    private final int accountId;

    public DatabaseHelper(Context context, int accountId) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.accountId = accountId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_MOVIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TMDB_ID + " INTEGER,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_YEAR + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_RATING + " REAL,"
                + COLUMN_RANKING + " INTEGER,"
                + COLUMN_REVIEW + " TEXT,"
                + COLUMN_IMG_URL + " TEXT,"
                + COLUMN_ACCOUNT_ID + " INTEGER"
                + ")";
        db.execSQL(CREATE_MOVIES_TABLE);

        String CREATE_TV_SERIES_TABLE = "CREATE TABLE " + TABLE_TV_SERIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TMDB_ID + " INTEGER,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_YEAR + " TEXT,"
                + COLUMN_FIRST_AIR_DATE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_RATING + " REAL,"
                + COLUMN_RANKING + " INTEGER,"
                + COLUMN_REVIEW + " TEXT,"
                + COLUMN_IMG_URL + " TEXT,"
                + COLUMN_ACCOUNT_ID + " INTEGER"
                + ")";
        db.execSQL(CREATE_TV_SERIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TV_SERIES);
        onCreate(db);
    }

    // TV Series methods
    public void addTvSeries(TvSeries tvSeries) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TMDB_ID, tvSeries.getTmdbId());
        values.put(COLUMN_NAME, tvSeries.getName());
        values.put(COLUMN_YEAR, tvSeries.getYear());
        values.put(COLUMN_FIRST_AIR_DATE, tvSeries.getFirstAirDate());
        values.put(COLUMN_DESCRIPTION, tvSeries.getDescription());
        values.put(COLUMN_RATING, tvSeries.getRating());
        values.put(COLUMN_RANKING, tvSeries.getRanking());
        values.put(COLUMN_REVIEW, tvSeries.getReview());
        values.put(COLUMN_IMG_URL, tvSeries.getImgUrl());
        values.put(COLUMN_ACCOUNT_ID, accountId);

        db.insert(TABLE_TV_SERIES, null, values);
        db.close();
    }

    public List<TvSeries> getAllTvSeries() {
        List<TvSeries> tvSeriesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TV_SERIES + 
                            " WHERE " + COLUMN_ACCOUNT_ID + " = " + accountId + 
                            " ORDER BY " + COLUMN_RATING + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TvSeries tvSeries = new TvSeries();
                tvSeries.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                tvSeries.setTmdbId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TMDB_ID)));
                tvSeries.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                tvSeries.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                tvSeries.setFirstAirDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_AIR_DATE)));
                tvSeries.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                tvSeries.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
                tvSeries.setRanking(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RANKING)));
                tvSeries.setReview(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW)));
                tvSeries.setImgUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_URL)));
                tvSeriesList.add(tvSeries);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tvSeriesList;
    }

    public TvSeries getTvSeries(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TV_SERIES,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            TvSeries tvSeries = new TvSeries();
            tvSeries.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            tvSeries.setTmdbId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TMDB_ID)));
            tvSeries.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            tvSeries.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
            tvSeries.setFirstAirDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_AIR_DATE)));
            tvSeries.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            tvSeries.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
            tvSeries.setRanking(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RANKING)));
            tvSeries.setReview(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW)));
            tvSeries.setImgUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_URL)));

            cursor.close();
            db.close();
            return tvSeries;
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    public int updateTvSeries(TvSeries tvSeries) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TMDB_ID, tvSeries.getTmdbId());
        values.put(COLUMN_NAME, tvSeries.getName());
        values.put(COLUMN_YEAR, tvSeries.getYear());
        values.put(COLUMN_FIRST_AIR_DATE, tvSeries.getFirstAirDate());
        values.put(COLUMN_DESCRIPTION, tvSeries.getDescription());
        values.put(COLUMN_RATING, tvSeries.getRating());
        values.put(COLUMN_RANKING, tvSeries.getRanking());
        values.put(COLUMN_REVIEW, tvSeries.getReview());
        values.put(COLUMN_IMG_URL, tvSeries.getImgUrl());

        int rowsAffected = db.update(TABLE_TV_SERIES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(tvSeries.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteTvSeries(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TV_SERIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Movie methods
    public void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TMDB_ID, movie.getTmdbId());
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_YEAR, movie.getYear());
        values.put(COLUMN_DESCRIPTION, movie.getDescription());
        values.put(COLUMN_RATING, movie.getRating());
        values.put(COLUMN_RANKING, movie.getRanking());
        values.put(COLUMN_REVIEW, movie.getReview());
        values.put(COLUMN_IMG_URL, movie.getImgUrl());
        values.put(COLUMN_ACCOUNT_ID, accountId);
        db.insert(TABLE_MOVIES, null, values);
        db.close();
    }

    public List<Movie> getAllMovies() {
        List<Movie> movieList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MOVIES + 
                           " WHERE " + COLUMN_ACCOUNT_ID + " = " + accountId + 
                           " ORDER BY " + COLUMN_RATING + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                movie.setTmdbId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TMDB_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                movie.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                movie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                movie.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
                movie.setRanking(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RANKING)));
                movie.setReview(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW)));
                movie.setImgUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_URL)));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return movieList;
    }

    public Movie getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIES,
                null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            movie.setTmdbId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TMDB_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            movie.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
            movie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            movie.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
            movie.setRanking(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RANKING)));
            movie.setReview(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REVIEW)));
            movie.setImgUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMG_URL)));

            cursor.close();
            db.close();
            return movie;
        }
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }

    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TMDB_ID, movie.getTmdbId());
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_YEAR, movie.getYear());
        values.put(COLUMN_DESCRIPTION, movie.getDescription());
        values.put(COLUMN_RATING, movie.getRating());
        values.put(COLUMN_RANKING, movie.getRanking());
        values.put(COLUMN_REVIEW, movie.getReview());
        values.put(COLUMN_IMG_URL, movie.getImgUrl());

        int rowsAffected = db.update(TABLE_MOVIES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(movie.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteMovie(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void syncFavoriteMovies(List<Movie> favoriteMovies) {
        // Misafir hesap için senkronizasyon yapma
        if (accountId == -999) return;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Mevcut filmleri temizle
            db.delete(TABLE_MOVIES, COLUMN_ACCOUNT_ID + " = ?", 
                new String[]{String.valueOf(accountId)});

            // Yeni filmleri ekle
            for (Movie movie : favoriteMovies) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TMDB_ID, movie.getTmdbId());
                values.put(COLUMN_TITLE, movie.getTitle());
                values.put(COLUMN_YEAR, movie.getYear());
                values.put(COLUMN_DESCRIPTION, movie.getDescription());
                values.put(COLUMN_RATING, movie.getRating());
                values.put(COLUMN_IMG_URL, movie.getImgUrl());
                values.put(COLUMN_ACCOUNT_ID, accountId);
                db.insert(TABLE_MOVIES, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void syncFavoriteTvSeries(List<TvSeries> favoriteTvSeries) {
        // Misafir hesap için senkronizasyon yapma
        if (accountId == -999) return;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Mevcut dizileri temizle
            db.delete(TABLE_TV_SERIES, COLUMN_ACCOUNT_ID + " = ?", 
                new String[]{String.valueOf(accountId)});

            // Yeni dizileri ekle
            for (TvSeries tvSeries : favoriteTvSeries) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TMDB_ID, tvSeries.getTmdbId());
                values.put(COLUMN_NAME, tvSeries.getName());
                values.put(COLUMN_YEAR, tvSeries.getYear());
                values.put(COLUMN_FIRST_AIR_DATE, tvSeries.getFirstAirDate());
                values.put(COLUMN_DESCRIPTION, tvSeries.getDescription());
                values.put(COLUMN_RATING, tvSeries.getRating());
                values.put(COLUMN_IMG_URL, tvSeries.getImgUrl());
                values.put(COLUMN_ACCOUNT_ID, accountId);
                db.insert(TABLE_TV_SERIES, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
}
