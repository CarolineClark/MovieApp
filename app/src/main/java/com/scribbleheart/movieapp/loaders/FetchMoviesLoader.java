package com.scribbleheart.movieapp.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.scribbleheart.movieapp.data.MovieFavouritesContract;
import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

import static com.scribbleheart.movieapp.data.MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE;


public class FetchMoviesLoader extends AsyncTaskLoader<MovieBean[]> {
    private MovieBean[] mMovieData;
    private String order;
    private String TAG = FetchMoviesLoader.class.getSimpleName();

    private FetchMoviesLoader(Context context) {
        super(context);
    }

    public FetchMoviesLoader(Context context, String order) {
        this(context);
        this.order = order;
    }

    @Override
    public MovieBean[] loadInBackground() {
        if (order.equals(Constants.FAVOURITES)) {
            return getFavouriteMovies();
        } else {
            return getMoviesFromNetwork();
        }
    }

    @Nullable
    private MovieBean[] getMoviesFromNetwork() {
        URL url = NetworkUtils.buildListOfMoviesUrl(order);
        Log.d(TAG, "Url = " + url);
        String jsonResponse;

        try {
            jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
            Log.v(TAG, "Response is " + jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            JSONArray resultsArray = new JSONObject(jsonResponse).getJSONArray("results");
            int length = resultsArray.length();
            MovieBean[] movies = new MovieBean[length];
            for (int i = 0; i< length; i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                movies[i] = new MovieBean(result);
            }
            return movies;
        } catch (JSONException e) {
            return null;
        }
    }

    private MovieBean[] getFavouriteMovies() {

        Uri uri = MovieFavouritesContract.MovieFavouritesEntry.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, COLUMN_TITLE);

        int sizeOfDb = cursor.getCount();
        MovieBean[] movies = new MovieBean[sizeOfDb];
        for (int i = 0; i< sizeOfDb; i++) {
            if (!cursor.moveToPosition(i)) {
                return movies;
            }
            movies[i] = new MovieBean(cursor);
        }

        cursor.close();
        return movies;
    }

    @Override
    protected void onStartLoading() {
        if (mMovieData != null) {
            deliverResult(mMovieData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(MovieBean[] data) {
        mMovieData = data;
        super.deliverResult(data);
    }
}
