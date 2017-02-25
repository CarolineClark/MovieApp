package com.scribbleheart.movieapp.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.scribbleheart.movieapp.BuildConfig;

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
    private static final String REVIEW_PARAM = "reviews";
    private static final String VIDEOS_PARAM = "trailers";

    public static URL buildListOfMoviesUrl(String order) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(order)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return getUrlFromUri(builtUri);
    }

    public static URL buildReviewMovieUrl(String id) {
        return buildMovieUrlForParam(id, REVIEW_PARAM);
    }

    public static URL buildVideosMovieUrl(String id) {
        return buildMovieUrlForParam(id, VIDEOS_PARAM);
    }

    private static URL buildMovieUrlForParam(String id, String param) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(param)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return getUrlFromUri(builtUri);
    }

    @Nullable
    private static URL getUrlFromUri(Uri builtUri) {
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
        return "w185";
    }

    static String constructImageUrl(String imageId) {
        Uri uri = Uri.parse(IMAGE_BASE_URL + "/" + getSize() + imageId);
        return uri.toString();
    }

    public static Uri createTrailerUri(String source) {
        return Uri.parse("https://youtu.be/" + source);
    }

    private static String TAG = NetworkUtils.class.getSimpleName();
}
