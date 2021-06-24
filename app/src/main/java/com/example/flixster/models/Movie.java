package com.example.flixster.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

@Parcel
public class Movie {

    public static final String TAG = "Movie";
    public static final String VIDEO = "https://api.themoviedb.org/3/movie/%s/videos?api_key=bd1591c106e1d2d6b0f1c487f82bd791";

    String title;
    String posterPath;
    String overview;
    String backdropPath;
    Double voteAverage;
    Integer id;
    String key;

    public Movie() {}

    // Initialize the title and details from the JSON data
    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        id = jsonObject.getInt("id");
        AsyncHttpClient client = new AsyncHttpClient();

        // Get the JSON data from the TMDB url
        client.get(String.format(VIDEO, id), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    key = results.getJSONObject(0).getString("key");
                } catch (JSONException e) {
                    key = null;
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    // Return a list of movies from a JSON array
    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<Movie>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    // Return the title of the movie
    public String getTitle() {
        return title;
    }

    // Return the relative path of the poster image of the movie
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    // Return the relative path of the backdrop image of the movie
    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w780/%s", backdropPath);
    }

    // Return the overview of the movie
    public String getOverview() {
        return overview;
    }

    // Return the voting average of the movie
    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
}
