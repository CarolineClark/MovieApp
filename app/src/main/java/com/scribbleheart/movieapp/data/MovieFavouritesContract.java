package com.scribbleheart.movieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieFavouritesContract {
    private MovieFavouritesContract() {}

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.scribbleheart.movieapp";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "movies";

    public static class MovieFavouritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMG_URL = "imageUrl";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RELEASE_URL_ID = "urlId";
    }
}
