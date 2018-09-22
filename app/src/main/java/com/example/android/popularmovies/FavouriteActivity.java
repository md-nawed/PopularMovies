package com.example.android.popularmovies;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class FavouriteActivity extends AppCompatActivity {

    private SimpleCursorAdapter adapter;
    public static final int MOVIE_LOADER_ID = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupCursorAdapter();
        ListView lvFavourite = (ListView) findViewById(R.id.lv_fav);
        lvFavourite.setAdapter(adapter);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,
                new Bundle(), favouritesLoader);
    }

    private void setupCursorAdapter() {
        String[] uiBindFrom = {MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_MOVIE_URL};
        int[] uiBindTo = {R.id.tvName, R.id.tvUrl};

        adapter = new SimpleCursorAdapter(this, R.layout.lv_item, null, uiBindFrom, uiBindTo, 0);
    }

    private LoaderManager.LoaderCallbacks<Cursor> favouritesLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String[] projectionFields = new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_URL};
                    CursorLoader cursorLoader = new CursorLoader(FavouriteActivity.this,
                            MovieContract.MovieEntry.CONTENT_URI,
                            projectionFields,
                            null,
                            null,
                            null
                    );
                    return cursorLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    adapter.swapCursor(data);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    adapter.swapCursor(null);
                }
            };
}
