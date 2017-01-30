package com.scribbleheart.movieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private String[] mImageUrls;
    private Context context;

    public MovieAdapter(Context context) {
        Picasso.with(context).setLoggingEnabled(true);
        this.context = context;
    }

    public void setMovieImage(String[] result) {
        mImageUrls = result;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mMovieImage;
//        public final TextView tv;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImage = (ImageView) view.findViewById(R.id.iv_movie_image);
//            tv = (TextView) view.findViewById(R.id.iv_movie_text);
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
        Log.d(TAG, "pictures are being loaded now with the URL " + mImageUrls[position]);
        String imageUrl = mImageUrls[position];
//        holder.tv.setText(imageUrl);
        Picasso.with(context)
                .load(imageUrl)
                .into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        if (mImageUrls == null) {
            return 0;
        }
        return mImageUrls.length;
    }

    private String TAG = MovieAdapter.class.getSimpleName();
}
