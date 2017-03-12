package com.scribbleheart.movieapp;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.scribbleheart.movieapp.utils.MovieBean;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<MovieBean> mMovies;
    private Context context;
    MovieAdapterClickHandler mClickHandler;

    public MovieAdapter(Context context, MovieAdapterClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }

    public void setMovies(List<MovieBean> movies) {
        mMovies = movies;
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
            MovieBean movie = mMovies.get(getAdapterPosition());
            mClickHandler.onClick(movie);
        }
    }

    public interface MovieAdapterClickHandler {
        void onClick(Parcelable movieInfo);
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
        String imageUrl = mMovies.get(position).getUrl();
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    private String TAG = MovieAdapter.class.getSimpleName();
}
