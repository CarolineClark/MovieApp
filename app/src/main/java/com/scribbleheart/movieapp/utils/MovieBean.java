package com.scribbleheart.movieapp.utils;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.scribbleheart.movieapp.data.MovieFavouritesContract;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieBean implements Parcelable {
    private String url;
    private String title;
    private String posterPath;
    private String rating;
    private String description;
    private String releaseDate;
    private String urlId;
    private long dbId;

    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_ID = "id";

    private static final long NO_DB_ENTRY = -1;

    protected MovieBean(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        rating = in.readString();
        description = in.readString();
        releaseDate = in.readString();
        url = in.readString();
        urlId = in.readString();
        dbId = in.readLong();
    }

    public MovieBean(JSONObject jsonObj) {
        try {
            title = jsonObj.getString(KEY_ORIGINAL_TITLE);
            posterPath = jsonObj.getString(KEY_POSTER_PATH);
            releaseDate = jsonObj.getString(KEY_RELEASE_DATE);
            rating = jsonObj.getString(KEY_VOTE_AVERAGE);
            description = jsonObj.getString(KEY_OVERVIEW);
            url = getMovieImageUrl(posterPath);
            urlId = jsonObj.getString(KEY_ID);
            dbId = NO_DB_ENTRY;
        } catch (JSONException e) {
            // don't do anything
        }
    }

    public MovieBean(Cursor cursor) {
        title = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE));
        description = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_DESCRIPTION));
        rating = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RATING));
        posterPath = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_POSTER_PATH));
        releaseDate = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_DATE));
        urlId = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_URL_ID));
        dbId = cursor.getLong(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry._ID));
        url = getMovieImageUrl(posterPath);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(rating);
        dest.writeString(description);
        dest.writeString(releaseDate);
        dest.writeString(url);
        dest.writeString(urlId);
        dest.writeLong(dbId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieBean> CREATOR = new Creator<MovieBean>() {
        @Override
        public MovieBean createFromParcel(Parcel in) {
            return new MovieBean(in);
        }

        @Override
        public MovieBean[] newArray(int size) {
            return new MovieBean[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    private String getMovieImageUrl(String posterPath) {
        if (posterPath != null) {
            return NetworkUtils.constructImageUrl(posterPath);
        }
        return null;
    }

    public String getUrl() {
        return url;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getUrlId() {
        return urlId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public long getDbId() {
        return dbId;
    }

    public boolean dbEntryExists() {
        // TODO two things could happen. We could create a movie bean from the cursor, or we could get it from the url.
        // This depends on the entry point, not on the movie.
        // Thus, we need a select statement?
        return !(dbId == NO_DB_ENTRY);

    }
}
