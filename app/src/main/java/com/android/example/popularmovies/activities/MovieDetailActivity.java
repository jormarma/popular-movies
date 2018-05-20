package com.android.example.popularmovies.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.popularmovies.R;
import com.android.example.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_EXTRA = "MOVIE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Movie movie = (Movie) getIntent().getSerializableExtra(MOVIE_EXTRA);

        ImageView moviePoster = findViewById(R.id.iv_movie_poster_detail);

        Picasso.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.loading)
                .error(R.drawable.forbidden)
                .into(moviePoster);

        TextView title = findViewById(R.id.tv_title);
        title.setText(movie.getTitle());
        TextView voteAverage = findViewById(R.id.tv_vote_average);
        voteAverage.setText(String.format(Locale.getDefault(), "%.1f", movie.getVoteAverage()));
        TextView releaseDate = findViewById(R.id.tv_release_date);
        releaseDate.setText(movie.getReleaseDate());
        TextView overview = findViewById(R.id.tv_overview);
        overview.setText(movie.getOverview());
    }
}
