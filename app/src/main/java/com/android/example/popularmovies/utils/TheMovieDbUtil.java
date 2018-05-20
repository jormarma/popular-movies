package com.android.example.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.example.popularmovies.R;
import com.android.example.popularmovies.data.Movie;
import com.android.example.popularmovies.data.MovieListResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class TheMovieDbUtil {

    private static final String THEMOVIEDB_TAG = "THEMOVIEDB";

    private final Context context;

    public TheMovieDbUtil(Context context) {
        this.context = context;
    }

    public MovieListResponse getMovieListResponse(String url) {
        Log.i(THEMOVIEDB_TAG, String.format("Request: %s", url));

        String result = "";
        HttpsURLConnection urlConnection = null;

        try {
            URL httpUrl = new URL(url);
            urlConnection = (HttpsURLConnection) httpUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            result = sb.toString();
            Log.i(THEMOVIEDB_TAG, String.format( "Response: %s", result));

        } catch (MalformedURLException e) {
            Log.e(THEMOVIEDB_TAG, String.format("The URL is not well formed: %s", e.getMessage()), e);

        } catch (IOException e) {
            Log.e(THEMOVIEDB_TAG, String.format("Error connecting to the endpoint: %s", e.getMessage()), e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return getResponse(result);
    }

    @NonNull
    private MovieListResponse getResponse(String result) {
        MovieListResponse movieListResponse = new MovieListResponse();

        try {
            JSONObject response = new JSONObject(result);
            movieListResponse.setPage(response.getInt("page"));
            movieListResponse.setTotalPagess(response.getInt("total_pages"));
            movieListResponse.setTotalResults(response.getInt("total_results"));

            JSONArray results = response.getJSONArray("results");
            List<Movie> movies = new ArrayList<>();
            movieListResponse.setMovies(movies);

            for (int i = 0; i < results.length(); i++) {
                Movie movie = new Movie();
                JSONObject jsonMovie = results.getJSONObject(i);
                movie.setVoteCount(jsonMovie.getLong("vote_count"));
                movie.setId(jsonMovie.getInt("id"));
                movie.setVideo(jsonMovie.getBoolean("video"));
                movie.setVoteAverage(jsonMovie.getDouble("vote_average"));
                movie.setTitle(jsonMovie.getString("title"));
                movie.setPopularity(jsonMovie.getDouble("popularity"));

                movie.setPosterPath(string(R.string.themoviedb_images_url) + string(R.string.themoviedb_list_poster_width) + jsonMovie.getString("poster_path"));

                movie.setOriginalLanguage(jsonMovie.getString("original_language"));
                movie.setOriginalTitle(jsonMovie.getString("original_title"));
                movie.setGenreIds(getGenreIds(jsonMovie));

                movie.setBackdropPath(jsonMovie.getString("backdrop_path"));
                movie.setAdult(jsonMovie.getBoolean("adult"));
                movie.setOverview(jsonMovie.getString("overview"));

                movie.setReleaseDate(jsonMovie.getString("release_date"));

                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e(THEMOVIEDB_TAG, String.format("Error processing the JSON response: %s", result), e);
        }
        return movieListResponse;
    }

    private long[] getGenreIds(JSONObject jsonMovie) throws JSONException {
        JSONArray jsonGenreIds = jsonMovie.getJSONArray("genre_ids");
        int length = jsonGenreIds.length();
        long[] genreIds = new long[length];

        for(int j = 0; j < length; j++) {
            genreIds[j] = jsonGenreIds.getLong(j);
        }

        return genreIds;
    }


    @NonNull
    public List<String> getPosterPathList(MovieListResponse movieListResponse) {
        List<String> posterURLs = new ArrayList<>();
        List<Movie> movies = movieListResponse.getMovies();

        if (movies != null) {
            for (Movie movie : movies) {
                posterURLs.add(movie.getPosterPath());
            }
        }

        logResponse(posterURLs);

        return posterURLs;
    }

    private void logResponse(List<String> posterURLs) {
        StringBuilder sb = new StringBuilder("Poster URLs:\n");

        for(String imagePath : posterURLs) {
            sb.append(imagePath).append("\n");
        }

        Log.v(TheMovieDbUtil.THEMOVIEDB_TAG, sb.toString());
    }


    public String getURL(String string) {
        return Uri.parse(string)
                .buildUpon()
                .appendQueryParameter("api_key", string(R.string.themoviedb_api_key))
                .build()
                .toString();
    }

    private String string(int key) {
        return context.getString(key);
    }

}
