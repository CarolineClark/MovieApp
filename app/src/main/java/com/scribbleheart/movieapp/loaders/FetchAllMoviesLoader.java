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
import java.util.ArrayList;
import java.util.List;

import static com.scribbleheart.movieapp.data.MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE;


public class FetchAllMoviesLoader extends AsyncTaskLoader<List<MovieBean>> {
    private List<MovieBean> mMovieData;
    private String order;
    private String TAG = FetchAllMoviesLoader.class.getSimpleName();

    private FetchAllMoviesLoader(Context context) {
        super(context);
    }

    public FetchAllMoviesLoader(Context context, String order) {
        this(context);
        this.order = order;
    }

    @Override
    public List<MovieBean> loadInBackground() {
        if (order.equals(Constants.FAVOURITES)) {
            return getFavouriteMovies();
        } else {
            return getMoviesFromNetwork();
        }
    }

    @Nullable
    private List<MovieBean> getMoviesFromNetwork() {
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
            List<MovieBean> movies = new ArrayList<>();
            for (int i = 0; i< length; i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                movies.add(new MovieBean(result));
            }
            return movies;
        } catch (JSONException e) {
            return null;
        }
    }

    private List<MovieBean> getFavouriteMovies() {

        Uri uri = MovieFavouritesContract.MovieFavouritesEntry.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, COLUMN_TITLE);

        int sizeOfDb = cursor.getCount();
        List<MovieBean> movies = new ArrayList<>();
        for (int i = 0; i< sizeOfDb; i++) {
            if (!cursor.moveToPosition(i)) {
                return movies;
            }
            movies.add(new MovieBean(cursor));
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
    public void deliverResult(List<MovieBean> data) {
        mMovieData = data;
        super.deliverResult(data);
    }
}
