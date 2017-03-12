package com.scribbleheart.movieapp.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.scribbleheart.movieapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class FetchSingleMovieTrailers extends AsyncTaskLoader<Uri[]> {

    private static final String TAG = FetchSingleMovieTrailers.class.getSimpleName();
    private final String id;
    private Uri[] mTrailers;

    public FetchSingleMovieTrailers(Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    public Uri[] loadInBackground() {
        URL trailerUrl = NetworkUtils.buildVideosMovieUrl(id);
        Log.d(TAG, "Trailer Url = " + trailerUrl);
        String trailerJsonResponse;

        try {
            trailerJsonResponse = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
            Log.v(TAG, "Trailer response is " + trailerJsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            return parseTrailerJsonResponse(trailerJsonResponse);
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    private Uri[] parseTrailerJsonResponse(String trailerJsonResponse) throws JSONException {
        JSONArray trailerArray = new JSONObject(trailerJsonResponse).getJSONArray("youtube");
        int length = trailerArray.length();
        Uri[] trailerUrls = new Uri[length];
        for (int i = 0; i< length; i++) {
            JSONObject trailerJson = trailerArray.getJSONObject(i);
            trailerUrls[i] = createTrailerUrl(trailerJson);
        }
        return trailerUrls;
    }

    private Uri createTrailerUrl(JSONObject trailerJson) throws JSONException {
        String source = trailerJson.getString("source");
        return NetworkUtils.createTrailerUri(source);
    }

    @Override
    protected void onStartLoading() {
        if (mTrailers != null) {
            deliverResult(mTrailers);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(Uri[] data) {
        mTrailers = data;
        super.deliverResult(data);
    }
}
