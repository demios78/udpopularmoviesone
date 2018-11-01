package com.snindustries.project.udacity.popularmovies.util;

import android.os.Build;

import com.snindustries.project.udacity.popularmovies.BuildConfig;
import com.snindustries.project.udacity.popularmovies.model.ConfigResponse;
import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public interface ImdbApi {

    @GET("configuration")
    Call<ConfigResponse> getConfiguration();

    @GET("movie/popular")
    Call<MovieSearchResponse> getMoviePopular(@Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieSearchResponse> getMovieTopRated(@Query("page") int page);

    @GET("/movie/{movie_id}")
    Call<Movie> getMovie(@Path("movie_id") int movieId);
}
