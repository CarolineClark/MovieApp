package com.scribbleheart.movieapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcelable;
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

import com.scribbleheart.movieapp.data.MovieFavouritesContract;
import com.scribbleheart.movieapp.data.MovieFavouritesDbHelper;
import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickHandler {

    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String selectedOrder;
    private GridLayoutManager layoutManager;
    private SQLiteDatabase mDb;

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

        MovieFavouritesDbHelper dbHelper = new MovieFavouritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        setInitialSelectedOrder();
        loadMoviesFromNetwork();
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

    private void loadMoviesFromNetwork() {
        showMovieDataView();
        // TODO: swap this for an AsyncTaskLoader
        new FetchMovieInformation().execute(selectedOrder);
    }

    private void loadMoviesFromDb() {
        mMovieAdapter.setMovies(getFavouriteMovies());
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
            loadMoviesFromNetwork();
            Log.d(TAG, "Refreshing the screen");
        } else if (item.getItemId() == R.id.sort_by_favourites) {
            // if this empty, should show a message like "No favourites chosen yet!"
            loadMoviesFromDb();
            Log.d(TAG, "sorting by favourites");
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadMoviesWithNewOrder(String newOrder) {
        if (!selectedOrder.equals(newOrder)) {
            selectedOrder = newOrder;
            loadMoviesFromNetwork();
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

    public class FetchMovieInformation extends AsyncTask<String, Void, MovieBean[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieBean[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String order = params[0];
            URL url = NetworkUtils.buildListOfMoviesUrl(order);
            Log.d(TAG, "Url = " + url);
            String jsonResponse;

            try {
                jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.v(TAG, "Response is " + jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                JSONArray resultsArray = new JSONObject(jsonResponse).getJSONArray("results");
                int length = resultsArray.length();
                MovieBean[] movies = new MovieBean[length];
                for (int i = 0; i< length; i++) {
                    JSONObject result = resultsArray.getJSONObject(i);
                    movies[i] = new MovieBean(result);
                }
                return movies;
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieBean[] movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies != null) {
                mMovieAdapter.setMovies(movies);
            } else {
                showErrorMessage();
                Log.v(TAG, "movie data is null! This is weird");
            }
        }
    }

    private MovieBean[] getFavouriteMovies() {
         Cursor cursor = mDb.query(
                MovieFavouritesContract.MovieFavouritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE
        );

        int sizeOfDb = cursor.getCount();
        MovieBean[] movies = new MovieBean[sizeOfDb];
        for (int i = 0; i< sizeOfDb; i++) {
            if (!cursor.moveToPosition(i)) {
                return movies;
            }

            String title = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_DESCRIPTION));
            String rating = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RATING));
            String posterPath = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_POSTER_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_DATE));
            String urlId = cursor.getString(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_URL_ID));

            movies[i] = new MovieBean(title, posterPath, releaseDate, description, urlId, rating);
        }

        cursor.close();
        return movies;
    }

    private String TAG = MainActivity.class.getSimpleName();
}
