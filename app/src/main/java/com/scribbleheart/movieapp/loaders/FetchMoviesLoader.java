package com.scribbleheart.movieapp.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;


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
