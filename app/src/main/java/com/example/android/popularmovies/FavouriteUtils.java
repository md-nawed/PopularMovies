package com.example.android.popularmovies;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class FavouriteUtils {
    private SQLiteDatabase db;

    private static final String mURLDomain = "https://api.themoviedb.org/3/movie/";


    public void favoriteCheck(Context context, int MovieIdInTMDB, boolean favoriteButtonChecked) {
        final String mKey = context.getString(R.string.key_themoviedb);
        MovieDbHelper mPopularMovieHelper = new MovieDbHelper(context);

        db = mPopularMovieHelper.getWritableDatabase();

        if (favoriteButtonChecked) {

            addFavorite(context, MovieIdInTMDB, mKey);

        } else {

            deleteFavorite(context, MovieIdInTMDB, mKey);

        }
    }

    private void deleteFavorite(Context context, int movieIdInTMDB, String mKey) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        String[] selectionArgs = {String.valueOf(movieIdInTMDB)};
        context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);

    }

    private void addFavorite(Context context, int movieIdInTMDB, String mKey) {
        ContentValues favoriteMovie = new ContentValues();

        favoriteMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, String.valueOf(movieIdInTMDB));

        favoriteMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_URL, mURLDomain + String.valueOf(movieIdInTMDB)
                + "?api_key=" + mKey + "&language=en-US");

        context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, favoriteMovie);
    }
}
