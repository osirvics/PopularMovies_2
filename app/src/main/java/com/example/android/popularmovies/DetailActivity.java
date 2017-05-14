package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.adapter.HorizontalVideoAdapter;
import com.example.android.popularmovies.api.TheMoviedbApiService;
import com.example.android.popularmovies.model.Results;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Reviews;
import com.example.android.popularmovies.model.Trailers;
import com.example.android.popularmovies.model.Youtube;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.api.TheMoviedbClient.getClient;

public class DetailActivity extends AppCompatActivity {
    private Results movie;
    private TextView releaseDate, ratings, title, plot;
    private ImageView poster;
    private Trailers videos;
    private ArrayList<Youtube> items;
    private ArrayList<Review> review;
    private Reviews reviews;
    private RecyclerView recyclerView;
    private HorizontalVideoAdapter adapter;


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
       // title = (TextView)findViewById(R.id.movie_title);
        plot = (TextView)findViewById(R.id.movie_plot);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
    }
    private void populateView(){
        String posterPath = movie.getPosterPath();
        Glide.with(this).load("http://image.tmdb.org/t/p/w342/" + posterPath).centerCrop().into(poster);
        //title.setText(movie.getTitle());
        ratings.setText(String.valueOf(movie.getVoteAverage()));
        Log.e("DetailActivity", "Vote Average: " + movie.getVoteAverage());
        releaseDate.setText(movie.getReleaseDate());
        plot.setText(movie.getOverview());
        String id = String.valueOf(movie.getId());

        displayTrailers(id);
        //loadReviews(id);
       // PersistData.cacheFavoritedata(this,movie);
    }

    private void displayTrailers(String id) {
//        if (items != null) {
//            //populateGrid();
//        } else {
            TheMoviedbApiService apiService =
                    getClient().create(TheMoviedbApiService.class);
            Call<Trailers> call;
                call = apiService.getTrailers(id,BuildConfig.MOVIE_API_KEY);
            call.enqueue(new Callback<Trailers>() {
                @Override
                public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                    if(response.code()== 200){

                        videos = new Trailers();
                        videos = response.body();
                        items= videos.getYoutube();
                        //items =  new ArrayList<>(videos.getTrailers());
                        Log.e("DetailActivity", "Size of videos are: " + items.size());
                       // populateGrid();
                       // int totalCount = items.getTotalPages();

                        /*recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapter = new HorizontalVideoAdapter(items, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (adapter.getItemCount() == 0) finish();
                            }
                        });*/
                    }
                }
                @Override
                public void onFailure(Call<Trailers> call, Throwable t) {
                    t.printStackTrace();
                    //showErrorView(t);
                }
            });
        //}
    }

    private void loadReviews(String id) {
//        if (items != null) {
//            //populateGrid();
//        } else {
            TheMoviedbApiService apiService =
                    getClient().create(TheMoviedbApiService.class);
            Call<Reviews> call;
            call = apiService.getReviews(id,BuildConfig.MOVIE_API_KEY);
            call.enqueue(new Callback<Reviews>() {
                @Override
                public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                    if(response.code()== 200){
                        review =  new ArrayList<>();
                        reviews = new Reviews();
                        reviews = response.body();
                        review = reviews.getResults();
                      //  Log.e("DetailActivity", "Size of videos are: " + items.size());
                        Log.e("DetailActivity", "Number of reviews are: " + review.size());
                        // populateGrid();
                        // int totalCount = items.getTotalPages();
                    }
                }
                @Override
                public void onFailure(Call<Reviews> call, Throwable t) {
                    t.printStackTrace();
                    //showErrorView(t);
                }
            });
       // }
    }



}
