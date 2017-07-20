package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC on 18-07-2017.
 */

public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private Double mVoteAverage;
    private String mReleaseDate;

    public Movie() {

    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public void setOverview(String overview) {
        if (!overview.equals("null"))
            mOverview = overview;
    }

    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;

    }

    public void setReleaseDate(String releaseDate) {
        if (!releaseDate.equals("null"))
            mReleaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getPosterPath() {
        final String POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";
        return POSTER_BASE_URL + mPosterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeValue(mVoteAverage);
    }

    private Movie(Parcel parcel) {
        mOriginalTitle = parcel.readString();
        mOverview = parcel.readString();
        mPosterPath = parcel.readString();
        mReleaseDate = parcel.readString();
        mVoteAverage = (Double) parcel.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
