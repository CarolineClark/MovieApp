package com.scribbleheart.movieapp.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_RESULTS = "results";

    public static String[] parseJsonResults(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray resultJsonArray = createJsonArray(jsonObject);

        return getListOfImageUrls(resultJsonArray);
//        return createListOfMovies(resultJsonArray);
    }

    public static JsonWrapper[] parseJsonResultsToWrapper(String jsonResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResponse);
        } catch (JSONException e) {
            return null;
        }
        JSONArray resultJsonArray = createJsonArray(jsonObject);
        int length = resultJsonArray.length();
        JsonWrapper[] list = new JsonWrapper[length];
        for(int i = 0; i < length; i++) {
            JSONObject jsonObject1;
            try {
                jsonObject1 = resultJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                Log.e(TAG, "Could not get jsonArray");
                jsonObject1 = null;
            }
            if (jsonObject1 != null) {
                list[i] = new JsonWrapper(jsonObject1);
            }
        }
        return list;
    }

    private static JSONArray createJsonArray(JSONObject jsonObject) {
        JSONArray resultJsonArray;

        if (jsonObject.has(KEY_RESULTS)) {
            try {
                resultJsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException e) {
                resultJsonArray = new JSONArray();
                Log.e(TAG, "Exception raised trying to get results from JsonArray");
            }
        } else if (jsonObject.has(KEY_POSTER_PATH)) {
            resultJsonArray = new JSONArray();
            resultJsonArray.put(jsonObject);
        } else {
            // TODO, should show "empty results" message
            resultJsonArray = new JSONArray();
        }
        return resultJsonArray;
    }

    @NonNull
    private static String[] getListOfImageUrls(JSONArray resultJsonArray) throws JSONException {
        int length = resultJsonArray.length();
        String[] list = new String[length];
        for(int i = 0; i < length; i++) {
            Log.d(TAG, String.valueOf(i));
            String poster_id = resultJsonArray.getJSONObject(i).getString(KEY_POSTER_PATH);
            String imageUrl = NetworkUtils.constructImageUrl(poster_id);
            Log.d(TAG, "In networkutils, " + imageUrl);
            list[i] = imageUrl;
        }
        return list;
    }

//    private static Movie[] createListOfMovies(JSONArray resultJsonArray) throws JSONException {
//        int length = resultJsonArray.length();
//        Movie[] list = new Movie[length];
//        for(int i = 0; i < length; i++) {
//            String poster_id = resultJsonArray.getJSONObject(i).getString(KEY_POSTER_PATH);
//            list[i] = new Movie.Builder()
//                    .posterId(poster_id)
//                    .build();
//        }
//        return list;
//    }

    private static String TAG = JsonParser.class.getSimpleName();
}
