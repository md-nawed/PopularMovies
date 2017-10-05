package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private RecyclerView mReviewsRecycleView;
    private RecyclerView mTrailerRecycleView;

    private ReviewsAdapter mReviewsAdapter;
    private String mMovieId;
    private static final String mBaseUrl = "https://api.themoviedb.org/3/movie/";


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
        mReviewsRecycleView = (RecyclerView) findViewById(R.id.rv_reviews);

        LinearLayoutManager linearLayoutManagerReviews = new LinearLayoutManager(this);
        linearLayoutManagerReviews.setOrientation(LinearLayoutManager.VERTICAL);
        mReviewsRecycleView.setLayoutManager(linearLayoutManagerReviews);


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
        Log.e("abcd", "url is: " + reviewsUrl);
        getReviews(reviewsUrl);
        mReviewsAdapter = new ReviewsAdapter(this, movieReviewsList);
        mReviewsRecycleView.setAdapter(mReviewsAdapter);


    }

    private void getReviews(String Url) {
        URL reviewsUrl = NetworkUtils.buildUrl(Url);
        new FetchDataTask().execute(reviewsUrl);
    }

    private class FetchDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.e("abc", "jsonresonse is: " + jsonResponse);

                return jsonResponse;
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
                mReviewsAdapter.setReviewData(movieReviewsList);
            }
            super.onPostExecute(response);
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