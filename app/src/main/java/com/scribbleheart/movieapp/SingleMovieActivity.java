package com.scribbleheart.movieapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.scribbleheart.movieapp.data.MovieFavouritesDbHelper;
import com.scribbleheart.movieapp.loaders.FetchMovieReviews;
import com.scribbleheart.movieapp.loaders.FetchMovieTrailers;
import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.scribbleheart.movieapp.utils.ReviewBean;
import com.squareup.picasso.Picasso;


public class SingleMovieActivity extends AppCompatActivity {
    private TextView title;
    private TextView overview;
    private TextView movieRating;
    private TextView releaseDate;
    private ImageView imageView;
    private LinearLayout trailerLinearLayout;
    private LinearLayout reviewLinearLayout;
    private MovieBean movieBean;
    private CheckBox mFavouritesCheckBox;
    private String urlId;

    private static String TAG = SingleMovieActivity.class.getSimpleName();
    private final int TRAILER_LOADER_ID = 11;
    private final int REVIEW_LOADER_ID = 12;
    private SQLiteDatabase mDb;

    private LoaderManager.LoaderCallbacks<ReviewBean[]> reviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<ReviewBean[]>() {

        @Override
        public Loader<ReviewBean[]> onCreateLoader(int id, Bundle args) {
            return new FetchMovieReviews(getApplicationContext(), urlId);
        }

        @Override
        public void onLoadFinished(Loader<ReviewBean[]> loader, ReviewBean[] data) {
            if (data != null) {
                for (final ReviewBean review: data) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(review.getContent());
                    reviewLinearLayout.addView(textView);
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
            return new FetchMovieTrailers(getApplicationContext(), urlId);
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
                    trailerLinearLayout.addView(button);
                }
            } else {
                Log.v(TAG, "No trailer info");
            }
        }

        @Override
        public void onLoaderReset(Loader<Uri[]> loader) {

        }
    };


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
        mFavouritesCheckBox = (CheckBox) findViewById(R.id.favourites_star);

        MovieFavouritesDbHelper dbHelper = new MovieFavouritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        movieBean = getBeanFromIntent();
        urlId = populateViewFrom(movieBean);
        LoaderManager manager = getSupportLoaderManager();
        manager.initLoader(TRAILER_LOADER_ID, null, trailerLoaderCallbacks);
        manager.initLoader(REVIEW_LOADER_ID, null, reviewLoaderCallbacks);
    }

    private String populateViewFrom(MovieBean movie) {
        final String urlId = movie.getUrlId();
        final String title = movie.getTitle();
        final String description = movie.getDescription();
        final String rating = movie.getRating();
        final String releaseDate = movie.getReleaseDate();
        final String posterPath = movie.getPosterPath();

        this.title.setText(title);
        overview.setText(description);
        movieRating.setText(rating);
        this.releaseDate.setText(releaseDate);
        String url = movie.getUrl();
        Picasso.with(this)
                .load(url)
                .into(imageView);

        return urlId;
    }

    private MovieBean getBeanFromIntent() {
        return getIntent().getExtras().getParcelable(Constants.MOVIE_JSON_KEY);
    }
}
