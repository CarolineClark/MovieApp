package com.scribbleheart.movieapp.data;

import android.provider.BaseColumns;

public class MovieFavouritesContract {
    private MovieFavouritesContract() {}

    public class MovieFavouritesEntry implements BaseColumns {
        public final static String TABLE_NAME = "movie";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_IMG_URL = "imageUrl";
        public final static String COLUMN_RATING = "rating";
        public final static String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RELEASE_URL_ID = "urlId";
    }
}
