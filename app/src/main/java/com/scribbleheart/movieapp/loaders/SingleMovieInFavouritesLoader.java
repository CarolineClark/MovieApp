package com.scribbleheart.movieapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.scribbleheart.movieapp.data.MovieFavouritesContract;

import static com.scribbleheart.movieapp.data.MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE;

public class SingleMovieInFavouritesLoader extends CursorLoader {
    private String urlId;

    public SingleMovieInFavouritesLoader(Context context, String urlId) {
        super(context);
        this.urlId = urlId;
    }

    @Override
    public Cursor loadInBackground() {
        Uri uri = MovieFavouritesContract.MovieFavouritesEntry.CONTENT_URI.buildUpon().appendPath(urlId).build();
        return getContext().getContentResolver().query(uri, null, null, null, COLUMN_TITLE);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);
    }
}
