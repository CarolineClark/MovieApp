package com.scribbleheart.movieapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scribbleheart.movieapp.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static com.scribbleheart.movieapp.utils.NetworkUtils.parseJsonResults;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String selectedOrder;
    private final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movies";
    private final String MOVIE_POPULAR_ORDER = "popular";
    private final String MOVIE_LATEST_ORDER = "latest";
    private final String API_KEY = "api_key";

//    private final int POPULAR_ORDER = R.string.sort_by_popularity;
//    private final String NEWEST_ORDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_movie_view);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.pg_loading);
        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.sort_by_popularity);
        item.setChecked(true);

        return true;
    }

    private void loadMovies() {
        showMovieDataView();
        new FetchMovieInformation().execute(selectedOrder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        selectedOrder = item.getTitle().toString();
        return super.onOptionsItemSelected(item);
    }

    private void showMovieDataView() {
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    public class FetchMovieInformation extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String order = params[0];
            URL url = NetworkUtils.buildMovieUrl(order);
            String jsonResponse = null;

            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.v(TAG, "Response is " + jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                return parseJsonResults(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                mMovieAdapter.setMovieImage(movieData);
            } else {
                Log.v(TAG, "movie data is null! This is weird");
            }
        }
    }

    private String TAG = MainActivity.class.getSimpleName();
}
