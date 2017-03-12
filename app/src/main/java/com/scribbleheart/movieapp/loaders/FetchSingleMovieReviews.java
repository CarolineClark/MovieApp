package com.scribbleheart.movieapp.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.scribbleheart.movieapp.utils.NetworkUtils;
import com.scribbleheart.movieapp.utils.ReviewBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class FetchSingleMovieReviews extends AsyncTaskLoader<ReviewBean[]> {

    private String TAG = FetchSingleMovieReviews.class.getSimpleName();
    private String id;
    private ReviewBean[] mReviewData;

    public FetchSingleMovieReviews(Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    public ReviewBean[] loadInBackground() {
        URL reviewUrl = NetworkUtils.buildReviewMovieUrl(id);
        Log.d(TAG, "Review Url = " + reviewUrl);
        String reviewJsonResponse;

        try {
            reviewJsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
            Log.v(TAG, "Review response is " + reviewJsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            return parseReviewJsonResponse(reviewJsonResponse);
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    private ReviewBean[] parseReviewJsonResponse(String reviewJsonResponse) throws JSONException {
        JSONArray reviewArray = new JSONObject(reviewJsonResponse).getJSONArray("results");
        int length = reviewArray.length();
        ReviewBean[] reviews = new ReviewBean[length];
        for (int i = 0; i< length; i++) {
            JSONObject reviewJson = reviewArray.getJSONObject(i);
            reviews[i] = new ReviewBean(reviewJson);
        }
        return reviews;
    }

    @Override
    protected void onStartLoading() {
        if (mReviewData != null) {
            deliverResult(mReviewData);
        } else {
            // make loading indicator visible
            forceLoad();
        }
    }

    @Override
    public void deliverResult(ReviewBean[] data) {
        mReviewData = data;
        super.deliverResult(data);
    }
}
