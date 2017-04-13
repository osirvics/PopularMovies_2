package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.model.Results;

public class DetailActivity extends AppCompatActivity {
    Results movie;
    TextView releaseDate, ratings, title, plot;
    ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        Intent intent = getIntent();

        if (intent.hasExtra("data")) {
            movie = new Results();
            movie = intent.getParcelableExtra("data");
        }
        setupView();
        populateView();
    }

    private void setupView(){
        poster = (ImageView)findViewById(R.id.detail_image);
        releaseDate = (TextView)findViewById(R.id.release_date);
        ratings = (TextView)findViewById(R.id.ratings);
        title = (TextView)findViewById(R.id.movie_title);
        plot = (TextView)findViewById(R.id.movie_plot);
    }
    private void populateView(){
        String posterPath = movie.getPosterPath();
        Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + posterPath).centerCrop().into(poster);
        title.setText(movie.getTitle());
        ratings.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
        plot.setText(movie.getOverview());
    }

}
