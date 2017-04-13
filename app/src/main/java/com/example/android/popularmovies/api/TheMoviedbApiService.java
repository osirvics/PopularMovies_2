package com.example.android.popularmovies.api;

import com.example.android.popularmovies.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Victor on 4/4/2017.
 */

public interface TheMoviedbApiService {

        @GET("movie/popular")
        Call<Movies> getPopularMovies(@Query("api_key") String api_key,
                                    @Query("page") int page);
        @GET("movie/top_rated")
        Call<Movies> getTopRatedMovies(@Query("api_key") String api_key,
                               @Query("page") int page);

}
