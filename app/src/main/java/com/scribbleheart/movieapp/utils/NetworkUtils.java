package com.scribbleheart.movieapp.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY_PARAM = "api_key";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    // TODO Enter your key here!
    private static final String USER_API_KEY = "your_api_key";

    public static URL buildMovieUrl(String order) {
        if (order == null) {
            // TODO: Raise exception, or trigger showing a dialog? This can be extended to whether
            // TODO: it is one of the values in the Constants class
        }

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(order)
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

    private static String getSize() {
        // TODO what happens when you make this bigger or smaller?
        return "w780";
    }

    public static String constructImageUrl(String imageId) {
        Uri uri = Uri.parse(IMAGE_BASE_URL + "/" + getSize() + imageId);
        return uri.toString();
    }

    private static String TAG = NetworkUtils.class.getSimpleName();
}
