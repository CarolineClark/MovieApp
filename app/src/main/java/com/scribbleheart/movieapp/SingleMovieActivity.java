package com.scribbleheart.movieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.scribbleheart.movieapp.utils.Constants;
import com.scribbleheart.movieapp.utils.MovieBean;
import com.squareup.picasso.Picasso;


public class SingleMovieActivity extends AppCompatActivity {
    private TextView title;
    private TextView overview;
    private TextView movieRating;
    private TextView releaseDate;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        title = (TextView) findViewById(R.id.movie_title);
        overview = (TextView) findViewById(R.id.movie_overview);
        movieRating = (TextView) findViewById(R.id.movie_vote_average);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        imageView = (ImageView) findViewById(R.id.movie_image);

        MovieBean movie = getBeanFromIntent();
        title.setText(movie.getTitle());
        overview.setText(movie.getDescription());
        movieRating.setText(movie.getRating());
        releaseDate.setText(movie.getReleaseDate());
        Picasso.with(this)
                .load(movie.getUrl())
                .into(imageView);
    }

    private MovieBean getBeanFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.MOVIE_JSON_KEY)) {
            return getIntent().getExtras().getParcelable(Constants.MOVIE_JSON_KEY);
        }
        return null;
    }
}
