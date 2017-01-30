package com.scribbleheart.movieapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String[] mImageUrls;

    public MovieAdapter() {

    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mMovieImage;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImage = (ImageView) view.findViewById(R.id.iv_movie_image);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_image_list_view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Picasso.with(holder.mMovieImage.getContext()).load("http://i.imgur.com/DvpvklR.png").into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        if (mImageUrls == null) {
            return 0;
        }
        return mImageUrls.length;
    }
}
