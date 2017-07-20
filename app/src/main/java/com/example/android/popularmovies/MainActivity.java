package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Menu mMenu;
    private int numberOfColumns = 2;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_num);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);

        //ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
        //      new ItemClickSupport.OnItemClickListener() {
        //
        //                  @Override
        //                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        //              Movie movie = (Movie) RecyclerView.ViewHolder.getItemView();
        //            }
        //      }
        //);


        if (savedInstanceState == null) {
            getMovies(getSortMethod());

        } else {
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(getString(R.string.parcel_movie));

            if (parcelables != null) {
                int numMovieObjects = parcelables.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelables[i];
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(new ImageAdapter(this, movies));


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;

        menu.add(Menu.NONE, R.id.item1, Menu.NONE, null)
                .setVisible(false)
                //.setIcon(R.drawable.ic_action_poll)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, R.id.item2, Menu.NONE, null)
                .setVisible(false)
                //.setIcon(R.drawable.ic_action_poll)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        updateMenu();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int numMovieObjects = recyclerView.getAdapter().getItemCount();
        if (numMovieObjects > 0) {
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {

                //movies[i]=(Movie) recyclerView.getAdapter().get
            }
        }
        super.onSaveInstanceState(outState);
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
            String apiKey = getString(R.string.key_themoviedb);
            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onFetchMoviesTaskCompleted(Movie[] movies) {
                    recyclerView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), numberOfColumns));

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
}
