package com.android.example.popularmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.popularmovies.R;
import com.android.example.popularmovies.adapters.MovieListAdapter;
import com.android.example.popularmovies.data.MovieListResponse;
import com.android.example.popularmovies.listeners.ListItemClickListener;
import com.android.example.popularmovies.utils.TheMovieDbUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mMovieListRecyclerView;
    private TextView mUnavailableDataTextView;
    private ImageView mUnavailableDataImageView;
    private ContentLoadingProgressBar mLoadingDataProgressBar;

    private TheMovieDbUtil theMovieDbUtil;

    private MovieListResponse moviesResponse;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieListRecyclerView = findViewById(R.id.rv_movie_list);
        mUnavailableDataTextView = findViewById(R.id.tv_unavailable_data);
        mUnavailableDataImageView = findViewById(R.id.iv_unavailable_data);
        mLoadingDataProgressBar = findViewById(R.id.iv_loading_data);

        theMovieDbUtil = new TheMovieDbUtil(this);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        loadMovies();
    }

    private void loadMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.pref_order_key);
        loadMovies(sharedPreferences, key);
    }

    private void loadMovies(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_order_key))) {
            String orderBy = sharedPreferences.getString(key, getString(R.string.pref_order_popular));
            new MoviesTask().execute(getString(R.string.themoviedb_base_url, orderBy));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                loadMovies();
                return true;

            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClickListener(int position) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_EXTRA, moviesResponse.getMovies().get(position));
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        loadMovies(sharedPreferences, key);
    }

    public class MoviesTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            String url = theMovieDbUtil.getURL(strings[0]);

            moviesResponse = theMovieDbUtil.getMovieListResponse(url);

            return theMovieDbUtil.getPosterPathList(moviesResponse);
        }

        @Override
        protected void onPreExecute() {
            mLoadingDataProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            mLoadingDataProgressBar.setVisibility(View.INVISIBLE);

            if (strings.isEmpty()) {
                mMovieListRecyclerView.setVisibility(View.INVISIBLE);
                mUnavailableDataTextView.setVisibility(View.VISIBLE);
                mUnavailableDataImageView.setVisibility(View.VISIBLE);

            } else {
                mUnavailableDataTextView.setVisibility(View.INVISIBLE);
                mUnavailableDataImageView.setVisibility(View.INVISIBLE);
                mMovieListRecyclerView.setVisibility(View.VISIBLE);
                mMovieListRecyclerView.setAdapter(new MovieListAdapter(strings, MainActivity.this));
            }
        }
    }

}
