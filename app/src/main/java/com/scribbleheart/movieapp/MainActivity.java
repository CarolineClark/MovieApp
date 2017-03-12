package com.scribbleheart.movieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scribbleheart.movieapp.loaders.FetchAllMoviesLoader;
import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler {

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String selectedOrder;
    private GridLayoutManager layoutManager;
    private int FETCH_MOVE_INFO_LOADER_ID = 1;

    private LoaderManager.LoaderCallbacks<List<MovieBean>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<MovieBean>>() {

        @Override
        public Loader<List<MovieBean>> onCreateLoader(int id, Bundle args) {
            return new FetchAllMoviesLoader(getApplicationContext(), selectedOrder);
        }

        @Override
        public void onLoadFinished(Loader<List<MovieBean>> loader, List<MovieBean>data) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (data != null) {
                showMovieDataView();
                mMovieAdapter.setMovies(data);
            } else {
                showErrorMessage();
                Log.v(TAG, "movie data is null! This is weird");
            }
        }

        @Override
        public void onLoaderReset(Loader<List<MovieBean>> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_movie_view);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);
        layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.pg_loading);

        setInitialSelectedOrder();
        getSupportLoaderManager().initLoader(FETCH_MOVE_INFO_LOADER_ID, null, loaderCallbacks);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setInitialSelectedOrder() {
        selectedOrder = Constants.TOP_RATED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item;
        if (selectedOrder.equals(Constants.TOP_RATED)) {
            item = menu.findItem(R.id.sort_by_top_rated);
        } else {
            item = menu.findItem(R.id.sort_by_popularity);
        }

        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        if (item.getItemId() == R.id.sort_by_top_rated) {
            reloadMoviesWithNewOrder(Constants.TOP_RATED);
            Log.d(TAG, "sorting by " + Constants.TOP_RATED);
        } else if (item.getItemId() == R.id.sort_by_popularity) {
            reloadMoviesWithNewOrder(Constants.POPULAR_ORDER);
            Log.d(TAG, "sorting by " + Constants.POPULAR_ORDER);
        } else if (item.getItemId() == R.id.refresh) {
            restartLoader();
            Log.d(TAG, "Refreshing the screen");
        } else if (item.getItemId() == R.id.sort_by_favourites) {
            // if this empty, should show a message like "No favourites chosen yet!"
            reloadMoviesWithNewOrder(Constants.FAVOURITES);
            Log.d(TAG, "sorting by favourites");
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeToSharedPrefs(String order) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("order", order);
        editor.apply();
    }

    private void restartLoader() {
        invalidateData();
        getSupportLoaderManager().restartLoader(FETCH_MOVE_INFO_LOADER_ID, null, loaderCallbacks);
    }

    private void invalidateData() {
        mMovieAdapter.setMovies(null);
    }

    private void reloadMoviesWithNewOrder(String newOrder) {
        if (!selectedOrder.equals(newOrder)) {
            // save order in SharedPreferences
            selectedOrder = newOrder;
            writeToSharedPrefs(newOrder);
            restartLoader();
        }
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
    public void onClick(Parcelable movieInfo) {
        Context context = this;
        Intent intent = new Intent(context, SingleMovieActivity.class);
        intent.putExtra(Constants.MOVIE_JSON_KEY, movieInfo);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FETCH_MOVE_INFO_LOADER_ID, null, loaderCallbacks);
    }

    private String TAG = MainActivity.class.getSimpleName();
}
