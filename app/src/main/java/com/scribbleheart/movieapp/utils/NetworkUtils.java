package com.scribbleheart.movieapp.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String MOVIE_POPULAR_ORDER = "popular";
    private static final String MOVIE_LATEST_ORDER = "latest";
    private static final String API_KEY_PARAM = "api_key";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    // TODO Enter your key here!
    private static final String USER_API_KEY = "your_api_key";
    private static final String KEY_POSTER_PATH = "poster_path";

    public static URL buildMovieUrl(String order) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(MOVIE_POPULAR_ORDER)
                .appendQueryParameter(API_KEY_PARAM, USER_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String[] parseJsonResults(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray resultJsonArray = jsonObject.getJSONArray("results");

        int length = resultJsonArray.length();
        String[] list = new String[length];
        for(int i = 0; i < length; i++) {
            Log.d(TAG, String.valueOf(i));
            String poster_id = resultJsonArray.getJSONObject(i).getString(KEY_POSTER_PATH);
            String imageUrl = constructImageUrl(poster_id);
            Log.d(TAG, "In networkutils, " + imageUrl);
            list[i] = imageUrl;
        }
        return list;
    }

    private static String getSize() {
        return "w185";
    }

    public static String constructImageUrl(String imageId) {
        Uri uri = Uri.parse(IMAGE_BASE_URL + "/" + getSize() + imageId);
        return uri.toString();
    }

    private static String TAG = NetworkUtils.class.getSimpleName();
}
