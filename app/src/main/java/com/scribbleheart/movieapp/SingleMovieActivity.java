package com.scribbleheart.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scribbleheart.movieapp.data.MovieFavouritesContract;
import com.scribbleheart.movieapp.loaders.SingleMovieInFavouritesLoader;
import com.scribbleheart.movieapp.loaders.FetchSingleMovieReviews;
import com.scribbleheart.movieapp.loaders.FetchSingleMovieTrailers;
import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.ReviewBean;
import com.squareup.picasso.Picasso;


public class SingleMovieActivity extends AppCompatActivity {
    private TextView mTitleTv;
    private TextView mOverviewTv;
    private TextView mMovieRatingTv;
    private TextView mReleaseDateTv;
    private ImageView mImageView;
    private LinearLayout mTrailerLinearLayout;
    private LinearLayout mReviewLinearLayout;
    private CheckBox mFavouritesCheckBox;
    private String urlId;

    private static String TAG = SingleMovieActivity.class.getSimpleName();
    private final int TRAILER_LOADER_ID = 11;
    private final int REVIEW_LOADER_ID = 12;
    private final int LOCAL_DB_LOADER_ID = 13;

    private LoaderManager.LoaderCallbacks<ReviewBean[]> reviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<ReviewBean[]>() {

        @Override
        public Loader<ReviewBean[]> onCreateLoader(int id, Bundle args) {
            return new FetchSingleMovieReviews(getApplicationContext(), urlId);
        }

        @Override
        public void onLoadFinished(Loader<ReviewBean[]> loader, ReviewBean[] data) {
            if (data != null) {
                for (final ReviewBean review: data) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(review.getContent());
                    mReviewLinearLayout.addView(textView);
                }
            } else {
                Log.v(TAG, "No review info");
            }
        }

        @Override
        public void onLoaderReset(Loader<ReviewBean[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Uri[]> trailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<Uri[]>() {

        @Override
        public Loader<Uri[]> onCreateLoader(int id, Bundle args) {
            return new FetchSingleMovieTrailers(getApplicationContext(), urlId);
        }

        @Override
        public void onLoadFinished(Loader<Uri[]> loader, Uri[] data) {
            if (data != null) {
                for (final Uri trailerUri : data) {
                    Button button = new Button(getApplicationContext());
                    button.setText(trailerUri.toString());
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, trailerUri));
                        }
                    });
                    mTrailerLinearLayout.addView(button);
                }
            } else {
                Log.v(TAG, "No trailer info");
            }
        }

        @Override
        public void onLoaderReset(Loader<Uri[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> dbLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new SingleMovieInFavouritesLoader(getApplicationContext(), urlId);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            boolean movieInDb = cursor.getCount() > 0;
            mFavouritesCheckBox.setChecked(movieInDb);
            final long dbId;
            if (movieInDb && cursor.moveToNext()) {
                dbId = cursor.getLong(cursor.getColumnIndex(MovieFavouritesContract.MovieFavouritesEntry._ID));
            } else {
                dbId = -1;
            }

            mFavouritesCheckBox.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ( ((CheckBox)view).isChecked() ) {
                                //insert
                                ContentValues contentValues = new ContentValues();
                                // Put the task description and selected mPriority into the ContentValues
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_TITLE, title);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_IMG_URL, imgUrl);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RATING, rating);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_DESCRIPTION, description);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_POSTER_PATH, posterPath);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_DATE, releaseDate);
                                contentValues.put(MovieFavouritesContract.MovieFavouritesEntry.COLUMN_RELEASE_URL_ID, urlId);

                                Uri uri = MovieFavouritesContract.MovieFavouritesEntry.CONTENT_URI;
                                getContentResolver().insert(uri, contentValues);
                            } else {
                                String stringId = Long.toString(dbId);
                                Uri uri = MovieFavouritesContract.MovieFavouritesEntry.CONTENT_URI;
                                uri = uri.buildUpon().appendPath(stringId).build();
                                getContentResolver().delete(uri, null, null);
                            }
                        }
                    }
            );
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };
    private String title;
    private String description;
    private String rating;
    private String releaseDate;
    private String imgUrl;
    private String posterPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        mTitleTv = (TextView) findViewById(R.id.movie_title);
        mOverviewTv = (TextView) findViewById(R.id.movie_overview);
        mMovieRatingTv = (TextView) findViewById(R.id.movie_vote_average);
        mReleaseDateTv = (TextView) findViewById(R.id.movie_release_date);
        mImageView = (ImageView) findViewById(R.id.movie_image);
        mTrailerLinearLayout = (LinearLayout) findViewById(R.id.trailer_linear_layout);
        mReviewLinearLayout = (LinearLayout) findViewById(R.id.reviews_linear_layout);
        mFavouritesCheckBox = (CheckBox) findViewById(R.id.favourites_star);

        MovieBean movieBean = getBeanFromIntent();
        urlId = movieBean.getUrlId();
        title = movieBean.getTitle();
        description = movieBean.getDescription();
        rating = movieBean.getRating();
        releaseDate = movieBean.getReleaseDate();
        posterPath = movieBean.getPosterPath();
        imgUrl = movieBean.getUrl();

        this.mTitleTv.setText(title);
        mOverviewTv.setText(description);
        mMovieRatingTv.setText(rating);
        this.mReleaseDateTv.setText(releaseDate);
        Picasso.with(this)
                .load(imgUrl)
                .into(mImageView);

        //TODO: if checkbox is checked, load from the database. Otherwise, load from the internet.
        LoaderManager manager = getSupportLoaderManager();
        manager.initLoader(LOCAL_DB_LOADER_ID, null, dbLoaderCallbacks);
        manager.initLoader(TRAILER_LOADER_ID, null, trailerLoaderCallbacks);
        manager.initLoader(REVIEW_LOADER_ID, null, reviewLoaderCallbacks);
    }

    private MovieBean getBeanFromIntent() {
        return getIntent().getExtras().getParcelable(Constants.MOVIE_JSON_KEY);
    }
}
