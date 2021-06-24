package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String KEY_VIDEO_ID = "video_id";

    Movie movie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        // Unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // Set the title and overview
        binding.tvTitle2.setText(movie.getTitle());
        binding.tvOverview2.setText(movie.getOverview());

        // Set the voting average
        // First convert from 0 - 10 to 0 - 5 scale
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage / 2.0f);

        String imageUrl;
        int placeholderId;
        // Set the preview image, backdrop if landscape, poster if portrait
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //If phone is landscape use backdrop image
            imageUrl = movie.getBackdropPath();
            placeholderId = R.drawable.flicks_backdrop_placeholder;
        } else {
            // else use the poster image
            imageUrl = movie.getPosterPath();
            placeholderId = R.drawable.flicks_movie_placeholder;
        }
        Glide.with(this).load(imageUrl).
                placeholder(placeholderId).
                into(binding.ivPreview);
        binding.ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.getKey() == null) {
                    return;
                }
                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                i.putExtra(KEY_VIDEO_ID, movie.getKey());
                startActivity(i);
            }
        });

    }
}