package com.scribbleheart.movieapp.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonWrapper {

    private JSONObject jsonObject;
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_OVERVIEW = "overview";

    public JsonWrapper(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonWrapper(String jsonString) {
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }
    }

    public String getPosterId() {
        return getStringFromJson(KEY_POSTER_PATH);
    }

    public String getOriginalTitle() {
        return getStringFromJson(KEY_ORIGINAL_TITLE);
    }

    public String getOverview() {
        return getStringFromJson(KEY_OVERVIEW);
    }

    public String getVoteAverage() {
        return getStringFromJson(KEY_VOTE_AVERAGE);
    }

    public String getReleaseDate() {
        return getStringFromJson(KEY_RELEASE_DATE);
    }

    public String getMovieImageUrl() {
        String posterId = getPosterId();
        if (posterId != null) {
            return NetworkUtils.constructImageUrl(posterId);
        }
        return null;
    }

    public String toString() {
        return jsonObject.toString();
    }

    private String getStringFromJson(String name) {
        try {
            return jsonObject.getString(name);
        } catch (JSONException e) {
            Log.e(TAG, "Could not get name " + name + "from JSON object");
            return null;
        }
    }

    private static String TAG = JsonWrapper.class.getSimpleName();
}
