package com.scribbleheart.movieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scribbleheart.movieapp.utils.JsonWrapper;
import com.squareup.picasso.Picasso;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private JsonWrapper[] mJsonMovies;
    private Context context;
    MovieAdapterClickHandler mClickHandler;

    public MovieAdapter(Context context, MovieAdapterClickHandler clickHandler) {
        Picasso.with(context).setLoggingEnabled(true);
        this.context = context;
        mClickHandler = clickHandler;
    }

    public void setJsonResponse(JsonWrapper[] response) {
        mJsonMovies = response;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mMovieImage;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImage = (ImageView) view.findViewById(R.id.iv_movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            JsonWrapper wrapper = mJsonMovies[getAdapterPosition()];
            mClickHandler.onClick(wrapper.toString());

        }
    }

    public interface MovieAdapterClickHandler {
        void onClick(String movieInformation);
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
        String imageUrl = mJsonMovies[position].getMovieImageUrl();
        Log.d(TAG, imageUrl);
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        if (mJsonMovies == null) {
            return 0;
        }
        return mJsonMovies.length;
    }

    private String TAG = MovieAdapter.class.getSimpleName();
}
