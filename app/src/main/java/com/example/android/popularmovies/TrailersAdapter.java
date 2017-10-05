package com.example.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private Context mContext;
    private List<MovieTrailers> mMovieTrailersList;
    private final List<String> mTrailerUrls = new ArrayList<>();
    private final List<String> mTrailerName = new ArrayList<>();
    private final List<String> mTrailerSize = new ArrayList<>();
    private final List<String> mTrailerType = new ArrayList<>();

    public TrailersAdapter(Context context, List<MovieTrailers> movieTrailersList) {
        mContext = context;
        mMovieTrailersList = movieTrailersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_trailer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        for (int i = 0; i < mMovieTrailersList.size(); i++) {
            mTrailerUrls.add("https://www.youtube.com/watch?v=" + mMovieTrailersList.get(i).getKey());
            mTrailerName.add(mMovieTrailersList.get(i).getName());
            mTrailerSize.add(mMovieTrailersList.get(i).getSize());
            mTrailerType.add(mMovieTrailersList.get(i).getType());
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String thumbnailUrl = "https://img.youtube.com/vi/" + mMovieTrailersList.get(position).getKey() + "/hqdefault.jpg";
        Picasso.with(mContext)
                .load(thumbnailUrl)
                .into(holder.mThumbnail);

        holder.mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mTrailerUrls.get(holder.getAdapterPosition()));
                Intent playIntent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(playIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mMovieTrailersList != null) ? mMovieTrailersList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mThumbnail;
        final ImageView mPlayButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mPlayButton = (ImageView) itemView.findViewById(R.id.play_button);

        }
    }

    public void setData(List<MovieTrailers> trailerData) {
        mMovieTrailersList = trailerData;
        notifyDataSetChanged();
    }
}
