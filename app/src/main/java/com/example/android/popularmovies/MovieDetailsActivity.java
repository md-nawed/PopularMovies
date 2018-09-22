package com.example.android.popularmovies;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MovieDetailsActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "extra_movie";
    private Movie movie;
    private List<MovieReviews> movieReviewsList = new ArrayList<>();
    private List<MovieTrailers> movieTrailersList = new ArrayList<>();
    private RecyclerView mReviewsRecycleView;
    private RecyclerView mTrailerRecycleView;

    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private String mMovieId;
    private static final String mBaseUrl = "https://api.themoviedb.org/3/movie/";
    private final FavouriteUtils mFavouriteUtils = new FavouriteUtils();


    private MovieDbHelper mMovieDbHelper;
    private SQLiteDatabase mDb;
    CheckBox mFavouriteCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        String mKey = getString(R.string.key_themoviedb);

        TextView tvOriginalTitle = (TextView) findViewById(R.id.textview_original_title);
        ImageView ivPoster = (ImageView) findViewById(R.id.imageview_poster);
        TextView tvOverView = (TextView) findViewById(R.id.textview_overview);
        TextView tvVoteAverage = (TextView) findViewById(R.id.textview_vote_average);
        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);
        TextView tvReview = (TextView) findViewById(R.id.textview_review_title);

        mFavouriteCheck = (CheckBox) findViewById(R.id.favourite_checkBox);
        mReviewsRecycleView = (RecyclerView) findViewById(R.id.rv_reviews);
        mTrailerRecycleView = (RecyclerView) findViewById(R.id.rv_trailers);

        LinearLayoutManager linearLayoutManagerReviews = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManagerTrailers = new LinearLayoutManager(this);

        linearLayoutManagerReviews.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManagerTrailers.setOrientation(LinearLayoutManager.HORIZONTAL);

        mReviewsRecycleView.setLayoutManager(linearLayoutManagerReviews);
        mTrailerRecycleView.setLayoutManager(linearLayoutManagerTrailers);


        Intent intent = getIntent();
        //Bundle b = this.getIntent().getExtras();
        if (intent.hasExtra(EXTRA_MOVIE)) {
            movie = intent.getParcelableExtra(EXTRA_MOVIE);
        }

        tvOriginalTitle.setText(movie.getOriginalTitle());

        Picasso.with(this)
                .load(movie.getPosterPath())
                .resize(getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .into(ivPoster);

        String overView = movie.getOverview();
        mMovieId = movie.getId();

        if (overView == null) {
            tvOverView.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_summary_found);
        }
        tvOverView.setText(overView);
        tvVoteAverage.setText(movie.getDetailedVoteAverage());

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

        String reviewsUrl = mBaseUrl + mMovieId + "/reviews?api_key=" + mKey;
        String trailersUrl = mBaseUrl + mMovieId + "/videos?api_key=" + mKey;


        getReviews(reviewsUrl);
        getTrailers(trailersUrl);

        mReviewsAdapter = new ReviewsAdapter(this, movieReviewsList);
        mTrailersAdapter = new TrailersAdapter(this, movieTrailersList);

        mReviewsRecycleView.setAdapter(mReviewsAdapter);
        mTrailerRecycleView.setAdapter(mTrailersAdapter);

        mMovieDbHelper = new MovieDbHelper(this);
        mDb = mMovieDbHelper.getWritableDatabase();

    }

    private void getTrailers(String Url) {
        URL trailersUrl = NetworkUtils.buildUrl(Url);
        new FetchDataTask().execute(trailersUrl);
    }

    private void getReviews(String Url) {
        URL reviewsUrl = NetworkUtils.buildUrl(Url);
        new FetchDataTask().execute(reviewsUrl);
    }

    public void onClickFavourite(View view) {
        boolean checked = mFavouriteCheck.isChecked();
        mFavouriteUtils.favoriteCheck(this, Integer.parseInt(mMovieId), checked);

    }

    private class FetchDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            try {

                return NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response.contains("author")) {

                try {
                    parseJsonReviews(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mReviewsAdapter.setData(movieReviewsList);
            } else if (response.contains("site")) {
                try {
                    parseJsonTrailers(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mTrailersAdapter.setData(movieTrailersList);
            }
            super.onPostExecute(response);
        }
    }

    private void parseJsonTrailers(String response) throws JSONException {
        final String RESULTS = "results";
        final String ID = "id";
        final String ISO_639_1 = "iso_639_1";
        final String ISO_3166_1 = "iso_3166_1";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size";
        final String TYPE = "type";
        JSONObject trailersJson = new JSONObject(response);
        JSONArray trailersArray = trailersJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailersArray.length(); i++) {
            JSONObject trailerInfo = trailersArray.getJSONObject(i);
            MovieTrailers trailers = new MovieTrailers();
            trailers.setId(trailerInfo.optString(ID));
            trailers.setIso_639_1(trailerInfo.optString(ISO_639_1));
            trailers.setIso_3166_1(trailerInfo.optString(ISO_3166_1));
            trailers.setKey(trailerInfo.optString(KEY));
            trailers.setName(trailerInfo.optString(NAME));
            trailers.setSite(trailerInfo.getString(SITE));
            trailers.setSize(trailerInfo.getString(SIZE));
            trailers.setType(trailerInfo.getString(TYPE));
            movieTrailersList.add(trailers);
        }
    }

    private void parseJsonReviews(String response) throws JSONException {
        final String RESULTS = "results";
        final String NAME = "author";
        final String CONTENT = "content";
        final String ID = "id";
        final String URL_ADDRESS = "url";

        JSONObject reviewsJson = new JSONObject(response);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject reviewInfo = reviewsArray.getJSONObject(i);
            MovieReviews reviews = new MovieReviews();
            reviews.setAuthor(reviewInfo.optString(NAME));
            reviews.setContent(reviewInfo.optString(CONTENT));
            reviews.setId(reviewInfo.optString(ID));
            reviews.setUrl(reviewInfo.optString(URL_ADDRESS));

            movieReviewsList.add(reviews);
        }
    }
}