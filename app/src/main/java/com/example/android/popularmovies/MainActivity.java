package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Menu mMenu;
    private RecyclerView recyclerView;
    private Movie[] movies;
    ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_num);
        recyclerView.setHasFixedSize(true); //size of view is fixed, height and width doesn't change.


        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        recyclerView.setLayoutManager(layoutManager);
        getMovies(getSortMethod());
        imageAdapter = new ImageAdapter(this, movies);

        recyclerView.setAdapter(imageAdapter);


        if (savedInstanceState == null) {
            getMovies(getSortMethod());

        } else {

            Parcelable[] parcelable = savedInstanceState.getParcelableArray("MOVIE_LIST");


            if (parcelable != null) {
                int numObjects = parcelable.length;
                Movie[] movies = new Movie[numObjects];
                for (int i = 0; i < numObjects; i++) {
                    movies[i] = (Movie) parcelable[i];
                }
                recyclerView.setAdapter(new ImageAdapter(this, movies));
            }

        }

    }

    private static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        menu.add(Menu.NONE, R.id.item1, Menu.NONE, null)
                .setVisible(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, R.id.item2, Menu.NONE, null)
                .setVisible(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        updateMenu();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray("MOVIE_LIST", movies);
        super.onSaveInstanceState(outState);
        int numMovieObjects = recyclerView.getAdapter().getItemCount();
        if (numMovieObjects > 0) {
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {

                //movies[i]=(Movie) recyclerView.getAdapter().get
                //movies[i] = ImageAdapter.getMov();
            }
        }

        //listState = recyclerView.getLayoutManager().onSaveInstanceState();
        //outState.putParcelable(STATUS_ID, listState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
                updateMenu();
                getMovies(getSortMethod());
                return true;
            case R.id.item2:
                updateSharedPrefs(getString(R.string.tmdb_sort_vote_avg_desc));
                updateMenu();
                getMovies(getSortMethod());
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {

        String sortMethod = getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_pop_desc))) {
            mMenu.findItem(R.id.item1).setVisible(false);
            mMenu.findItem(R.id.item2).setVisible(true);

        } else {
            mMenu.findItem(R.id.item2).setVisible(false);
            mMenu.findItem(R.id.item1).setVisible(true);
        }
    }

    private void getMovies(String sortMethod) {
        if (isNetworkAvailable()) {
            String apiKey = getString(R.string.key_themoviedb); //Insert api key
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onFetchMoviesTaskCompleted(Movie[] movies) {
                    imageAdapter.addAll(movies);

                }
            };
            FetchMovieTask movieTask = new FetchMovieTask(taskCompleted, apiKey);
            movieTask.execute(sortMethod);

        } else {
            Toast.makeText(this, getString(R.string.error_need_internet), Toast.LENGTH_LONG).show();

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_pop_desc));
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    public static void OnClicked(Context context, Movie movie) {

        Intent intent = new Intent(context, MovieDetailsActivity.class);

        //Bundle b = new Bundle();
        //b.putParcelable(String.valueOf(R.string.parcel_movie), movie);
        //intent.putExtras(b);

        intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
        context.startActivity(intent);
    }
}
