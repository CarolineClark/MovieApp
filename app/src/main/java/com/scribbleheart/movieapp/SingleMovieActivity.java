package com.scribbleheart.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.NetworkUtils;
import com.scribbleheart.movieapp.utils.ReviewBean;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class SingleMovieActivity extends AppCompatActivity {
    private TextView title;
    private TextView overview;
    private TextView movieRating;
    private TextView releaseDate;
    private ImageView imageView;
    private LinearLayout trailerLinearLayout;
    private LinearLayout reviewLinearLayout;
    private ProgressBar mProgressBar;
    private MovieBean movieBean;


    private static String TAG = SingleMovieActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        title = (TextView) findViewById(R.id.movie_title);
        overview = (TextView) findViewById(R.id.movie_overview);
        movieRating = (TextView) findViewById(R.id.movie_vote_average);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        imageView = (ImageView) findViewById(R.id.movie_image);
        trailerLinearLayout = (LinearLayout) findViewById(R.id.trailer_linear_layout);
        reviewLinearLayout = (LinearLayout) findViewById(R.id.reviews_linear_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.single_movie_progress_bar);

        movieBean = getBeanFromIntent();

        if (movieBean != null) {
            title.setText(movieBean.getTitle());
            overview.setText(movieBean.getDescription());
            movieRating.setText(movieBean.getRating());
            releaseDate.setText(movieBean.getReleaseDate());
            Picasso.with(this)
                    .load(movieBean.getUrl())
                    .into(imageView);
        }

        new FetchTrailerInformation().execute(movieBean.getId());
        new FetchReviewInformation().execute(movieBean.getId());
    }

    private MovieBean getBeanFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.MOVIE_JSON_KEY)) {
            return intent.getExtras().getParcelable(Constants.MOVIE_JSON_KEY);
        }
        return null;
    }

    public class FetchTrailerInformation extends AsyncTask<String, Void, Uri[]> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Uri[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String id = params[0];
            URL trailerUrl = NetworkUtils.buildVideosMovieUrl(id);
            Log.d(TAG, "Trailer Url = " + trailerUrl);
            String trailerJsonResponse;

            try {
                trailerJsonResponse = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                Log.v(TAG, "Trailer response is " + trailerJsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                return parseTrailerJsonResponse(trailerJsonResponse);
            } catch (JSONException e) {
                return null;
            }
        }

        @NonNull
        private Uri[] parseTrailerJsonResponse(String trailerJsonResponse) throws JSONException {
            JSONArray trailerArray = new JSONObject(trailerJsonResponse).getJSONArray("youtube");
            int length = trailerArray.length();
            Uri[] trailerUrls = new Uri[length];
            for (int i = 0; i< length; i++) {
                JSONObject trailerJson = trailerArray.getJSONObject(i);
                trailerUrls[i] = createTrailerUrl(trailerJson);
            }
            return trailerUrls;
        }

        @Override
        protected void onPostExecute(Uri[] trailers) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (trailers != null) {
                for (final Uri trailerUri : trailers) {
                    Button button = new Button(getApplicationContext());
                    button.setText(trailerUri.toString());
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
                        }
                    });
                    trailerLinearLayout.addView(button);
                }
            } else {
                // no trailers to show!
                Log.v(TAG, "No trailer info");
            }
        }
    }


    public class FetchReviewInformation extends AsyncTask<String, Void, ReviewBean[]> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected ReviewBean[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String id = params[0];
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
        protected void onPostExecute(ReviewBean[] reviews) {
            if (reviews != null) {
                for (final ReviewBean review : reviews) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(review.getContent());
                    reviewLinearLayout.addView(textView);
                }
            } else {
                Log.v(TAG, "No review info");
            }
        }
    }

    private Uri createTrailerUrl(JSONObject trailerJson) throws JSONException {
        String source = trailerJson.getString("source");
        return NetworkUtils.createTrailerUri(source);
    }
}
