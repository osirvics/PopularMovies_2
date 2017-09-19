package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.adapter.HorizontalVideoAdapter;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.api.TheMoviedbApiService;
import com.example.android.popularmovies.model.PersistData;
import com.example.android.popularmovies.model.Results;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Reviews;
import com.example.android.popularmovies.model.Video;
import com.example.android.popularmovies.model.Videos;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.api.TheMoviedbClient.getClient;

public class DetailActivity extends AppCompatActivity {
    private Results movie;
    private TextView releaseDate, ratings, title, plot;
    private ImageView poster;
    private Videos videos;
    private ArrayList<Video> items;
    private ArrayList<Review> review;
    private Reviews reviews;
    private RecyclerView recyclerView, reviewsView;
    private HorizontalVideoAdapter adapter;
    private ReviewAdapter reviewAdapter;
    private FloatingActionButton fab;
    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;


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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersistData.cacheFavoriteMovieData(getApplicationContext(),movie);
            }
        });
        setmBottomSheetBehaviorCallback();
    }

    private void setupView(){
        poster = (ImageView)findViewById(R.id.detail_image);
        releaseDate = (TextView)findViewById(R.id.release_date);
        ratings = (TextView)findViewById(R.id.ratings);
       title = (TextView)findViewById(R.id.title);
        plot = (TextView)findViewById(R.id.movie_plot);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        reviewsView = (RecyclerView)findViewById(R.id.review_recycler);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        bottomSheet = findViewById(R.id.reviews_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        //mBottomSheetBehavior.setPeekHeight(300);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    private void populateView(){
        String posterPath = movie.getPosterPath();
        Glide.with(this).load("http://image.tmdb.org/t/p/w342/" + posterPath).centerCrop().into(poster);
        title.setText(movie.getTitle());
        ratings.setText(String.valueOf(movie.getVoteAverage()));
        Log.e("DetailActivity", "Vote Average: " + movie.getVoteAverage());
        releaseDate.setText(movie.getReleaseDate());
        plot.setText(movie.getOverview());
        String id = String.valueOf(movie.getId());
        displayTrailers(id);
        loadReviews(id);
    }

    private void displayTrailers(String id) {
//        if (items != null) {
//            //populateGrid();
//        } else {
            TheMoviedbApiService apiService =
                    getClient().create(TheMoviedbApiService.class);
            Call<Videos> call;
                call = apiService.getTrailers(id,BuildConfig.MOVIE_API_KEY);
            call.enqueue(new Callback<Videos>() {
                @Override
                public void onResponse(Call<Videos> call, Response<Videos> response) {
                    if(response.code()== 200){

                        videos = new Videos();
                        videos = response.body();
                        items= videos.getYoutube();
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        adapter = new HorizontalVideoAdapter(items, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (adapter.getItemCount() == 0) finish();
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<Videos> call, Throwable t) {
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


                        reviewsView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        reviewAdapter = new ReviewAdapter(review);
                        reviewsView.setAdapter(reviewAdapter);
                        reviewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if (reviewAdapter.getItemCount() == 0) finish();
                            }
                        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
            } else if (item.getItemId() == R.id.action_review) {
            if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fab.hide();
               // mButton1.setText(R.string.collapse_button1);
            }
            else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                fab.show();
                //mButton1.setText(R.string.button1);
            }
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

    private void setmBottomSheetBehaviorCallback(){
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fab.hide();
                }
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    fab.show();
                }
                else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    fab.show();
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
    }




}
