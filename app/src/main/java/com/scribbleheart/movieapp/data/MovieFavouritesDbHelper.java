package com.scribbleheart.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.scribbleheart.movieapp.data.MovieFavouritesContract.MovieFavouritesEntry;

public class MovieFavouritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie_favourites.db";
    private static final int DATABASE_VERSION = 2;

    public MovieFavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + MovieFavouritesEntry.TABLE_NAME + " (" +
                MovieFavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieFavouritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieFavouritesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MovieFavouritesEntry.COLUMN_RATING + " INTEGER NOT NULL, " +
                MovieFavouritesEntry.COLUMN_IMG_URL + " TEXT NOT NULL, " +
                MovieFavouritesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieFavouritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieFavouritesEntry.COLUMN_RELEASE_URL_ID + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieFavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
