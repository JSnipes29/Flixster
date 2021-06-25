package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    // URL with JSON data on movies playing now from TMDB
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/%s?api_key=bd1591c106e1d2d6b0f1c487f82bd791";
    public static final String GENRES_URL = "https://api.themoviedb.org/3/discover/movie?api_key=bd1591c106e1d2d6b0f1c487f82bd791&with_genres=%s";
    public static final String GENERIC_GENRES_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=bd1591c106e1d2d6b0f1c487f82bd791";
    public static final String TAG = "MainActivity";
    public static final String[] TRENDING = {"now playing", "popular", "top rated", "upcoming"};

    List<Movie> movies;
    String movieOption;
    MovieAdapter movieAdapter;
    ArrayAdapter<Integer> idAdapter;

    // Genre adapter
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        movies = new ArrayList<>();

        // Create the adapter
        movieAdapter = new MovieAdapter(this, movies);
        // Set the adapter on the recycler view
        binding.rvMovies.setAdapter(movieAdapter);
        // Set a Layout Manager on the recycler view
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
        movies.size();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        /**ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movie_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); **/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("Now Playing");
        adapter.add("Popular");
        adapter.add("Top Rated");
        adapter.add("Upcoming");
        idAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GENERIC_GENRES_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("genres");
                    Log.i(TAG, "Genre: " + results.toString());
                    for (int j = 0; j < results.length(); j++) {
                        adapter.add(results.getJSONObject(j).getString("name"));
                        idAdapter.add(results.getJSONObject(j).getInt("id"));
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                movies.clear();
                movieOption = parent.getItemAtPosition(position).toString();
                AsyncHttpClient client = new AsyncHttpClient();
                updateList(movieOption, client, movieAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        return true;
    }

    private void updateList(String option, AsyncHttpClient client, MovieAdapter movieAdapter) {
        // Get the JSON data from the TMDB url
        client.get(formatUrl(option), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG,"onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });


    }
    private String formatUrl(String query) {
        if (Arrays.asList(TRENDING).contains(query.toLowerCase())) {
            return String.format(NOW_PLAYING_URL, query.replaceAll(" ", "_").toLowerCase());
        }
        Integer id = 12;
        int position = adapter.getPosition(query);
        id = idAdapter.getItem(position);
        return String.format(GENRES_URL, id);
    }


}