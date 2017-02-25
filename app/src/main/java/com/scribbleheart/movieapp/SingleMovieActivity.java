package com.scribbleheart.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
        mProgressBar = (ProgressBar) findViewById(R.id.single_movie_progress_bar);

        movieBean = getBeanFromIntent();
        new FetchTrailerInformation().execute(movieBean.getId());
    }

    private void postLoadMovieInfo() {
        title.setText(movieBean.getTitle());
        overview.setText(movieBean.getDescription());
        movieRating.setText(movieBean.getRating());
        releaseDate.setText(movieBean.getReleaseDate());
        Picasso.with(this)
                .load(movieBean.getUrl())
                .into(imageView);
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
            URL url = NetworkUtils.buildVideosMovieUrl(id);
            Log.d(TAG, "Url = " + url);
            String jsonResponse;

            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.v(TAG, "Trailer response is " + jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                JSONArray trailerArray = new JSONObject(jsonResponse).getJSONArray("youtube");
                int length = trailerArray.length();
                Uri[] trailerUrls = new Uri[length];
                for (int i = 0; i< length; i++) {
                    JSONObject trailerJson = trailerArray.getJSONObject(i);
                    trailerUrls[i] = createTrailerUrl(trailerJson);
                }
                return trailerUrls;
            } catch (JSONException e) {
                return null;
            }
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
            postLoadMovieInfo();
        }
    }

    private Uri createTrailerUrl(JSONObject trailerJson) throws JSONException {
        String source = trailerJson.getString("source");
        return NetworkUtils.createTrailerUri(source);
    }
}
