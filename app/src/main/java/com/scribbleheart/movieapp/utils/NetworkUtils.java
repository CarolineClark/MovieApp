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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String MOVIE_POPULAR_ORDER = "popular";
    private static final String MOVIE_LATEST_ORDER = "latest";
    private static final String API_KEY_PARAM = "api_key";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    // TODO Enter your key here!
    private static final String USER_API_KEY = "your_api_key";

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

    public static List<String> parseJsonResults(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray resultJsonArray = jsonObject.getJSONArray("results");

        List<String> list = new ArrayList<>();
        for(int i = 0; i < resultJsonArray.length(); i++){
            list.add(resultJsonArray.getJSONObject(i).toString());
        }
        return list;
    }

    private static String getSize() {
        return "w185";
    }

    public static String constructImageUrl(String imageId) {
        Uri uri = new Uri.Builder()
                .appendPath(IMAGE_BASE_URL)
                .appendPath(getSize())
                .appendPath(imageId)
                .build();
        return uri.toString();
    }

    private static String TAG = NetworkUtils.class.getSimpleName();
}
