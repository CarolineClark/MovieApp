package com.scribbleheart.movieapp.utils;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


public class MovieBean implements Parcelable {
    private String url;
    private String title;
    private String posterPath;
    private String rating;
    private String description;
    private String releaseDate;
    private String id;

    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_ID = "id";

    protected MovieBean(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        rating = in.readString();
        description = in.readString();
        releaseDate = in.readString();
        url = in.readString();
        id = in.readString();
    }

    public MovieBean(JSONObject jsonObj) {
        try {
            title = jsonObj.getString(KEY_ORIGINAL_TITLE);
            posterPath = jsonObj.getString(KEY_POSTER_PATH);
            releaseDate = jsonObj.getString(KEY_RELEASE_DATE);
            rating = jsonObj.getString(KEY_VOTE_AVERAGE);
            description = jsonObj.getString(KEY_OVERVIEW);
            url = getMovieImageUrl();
            id = jsonObj.getString(KEY_ID);
        } catch (JSONException e) {
            // don't do anything
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(rating);
        dest.writeString(description);
        dest.writeString(releaseDate);
        dest.writeString(url);
        dest.writeString(id);
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

    private String getMovieImageUrl() {
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

    public String getId() {
        return id;
    }
}
