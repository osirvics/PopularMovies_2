package com.example.android.popularmovies.api;

import com.example.android.popularmovies.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Victor on 4/4/2017.
 */

public interface TheMoviedbApiService {

        @GET("3/discover/movie?")
        Call<Movies> getMovies(@Query("api_key") String api_key,
                                    @Query("sort_by") String sort_by,
                                    @Query("page") int page);


}
