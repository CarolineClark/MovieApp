package com.scribbleheart.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.NetworkUtils;

import org.json.JSONException;

import java.net.URL;

import static com.scribbleheart.movieapp.utils.NetworkUtils.parseJsonResults;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler {

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String selectedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_movie_view);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
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
        if (item.getItemId() == R.id.sort_by_newest) {
            selectedOrder = Constants.LATEST_ORDER;
        } else if (item.getItemId() == R.id.sort_by_popularity) {
            selectedOrder = Constants.POPULAR_ORDER;
        }
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

    @Override
    public void onClick(String movieInformation) {
        Context context = this;
        Intent intent = new Intent(context, SingleMovieActivity.class);
        startActivity(intent);
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
                showErrorMessage();
                Log.v(TAG, "movie data is null! This is weird");
            }
        }
    }

    private String TAG = MainActivity.class.getSimpleName();
}
