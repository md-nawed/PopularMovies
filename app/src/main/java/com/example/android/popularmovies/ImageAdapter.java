package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by PC on 18-07-2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    public final Movie[] mMovies;

    public ImageAdapter(Context context, Movie[] movies) {
        mContext = context;
        mMovies = movies;
    }


    //private Context getContext() {
    //    return mContext;
    // }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View imageViews = inflater.inflate(R.layout.item, parent, false);

        ViewHolder viewHolder = new ViewHolder(imageViews);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {

        Picasso.with(mContext)
                .load(mMovies[position].getPosterPath())
                .resize(mContext.getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        mContext.getResources().getInteger(R.integer.tmdb_poster_w185_height))
                //.error(R.drawable.not_found)
                //.placeholder(R.drawable.searching)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return mMovies.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = new Movie();
                movie = mMovies[position];
                MainActivity.OnClicked(v.getContext(), movie);


            }

        }
    }
}
