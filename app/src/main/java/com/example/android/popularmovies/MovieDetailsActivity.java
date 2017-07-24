package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

/**
 * Created by PC on 19-07-2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private TextView tvOriginalTitle;
    private ImageView ivPoster;
    private TextView tvOverView;
    private TextView tvVoteAverage;
    private TextView tvReleaseDate;
    private Movie movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        tvOriginalTitle = (TextView) findViewById(R.id.textview_original_title);
        if (tvOriginalTitle == null) {
            Log.w("", "TVoriginalTitle is null");
        }
        ivPoster = (ImageView) findViewById(R.id.imageview_poster);
        if (ivPoster == null) {
            Log.w("", "ImageView is null");
        }
        tvOverView = (TextView) findViewById(R.id.textview_overview);
        if (tvOverView == null) {
            Log.w("", "Overview is null");
        }
        tvVoteAverage = (TextView) findViewById(R.id.textview_vote_average);
        if (tvVoteAverage == null) {
            Log.w("", "voteaverage is null");
        }
        tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        if (tvReleaseDate == null) {
            Log.w("", "releasedate is null");
        }

        Intent intent = getIntent();
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            movie = b.getParcelable(getString(R.string.parcel_movie));
        }


        //Movie movie = (Movie) intent.getParcelableExtra(getString(R.string.parcel_movie));

        Log.d(LOG_TAG, "new movie  :" + movie);


        tvOriginalTitle.setText(movie.getOriginalTitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
                .resize(getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        getResources().getInteger(R.integer.tmdb_poster_w185_height))
                //.error(R.drawable.not_found)
                //.placeholder(R.drawable.searching)
                .into(ivPoster);

        String overView = movie.getOverview();
        Log.d(LOG_TAG, " overview :" + overView);

        if (overView == null) {
            tvOverView.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_summary_found);
        }
        tvOverView.setText(overView);
        tvVoteAverage.setText(movie.getDetailedVoteAverage());

        // First get the release date from the object - to be used if something goes wrong with
        // getting localized release date (catch).
        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null) {
            try {
                releaseDate = DateTimeHelper.getLocalizedDate(this,
                        releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error with parsing movie release date", e);
            }
        } else {
            tvReleaseDate.setTypeface(null, Typeface.ITALIC);
            releaseDate = getResources().getString(R.string.no_release_date_found);
        }
        tvReleaseDate.setText(releaseDate);
    }
}
