package com.example.android.popularmovies;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context mContext;
    private List<MovieReviews> mMovieReviewsList;

    public ReviewsAdapter(Context context, List<MovieReviews> movieReviewsList) {
        mContext = context;
        mMovieReviewsList = movieReviewsList;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textview_item);

        }
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        MovieReviews movieReviews = mMovieReviewsList.get(position);
        holder.mTextView.setText("Author: " + movieReviews.getAuthor() + "\n" + movieReviews.getContent());
    }

    @Override
    public int getItemCount() {
        return (mMovieReviewsList != null) ? mMovieReviewsList.size() : 0;
    }

    public void setData(List<MovieReviews> reviewData) {
        mMovieReviewsList = reviewData;
        notifyDataSetChanged();
    }
}
