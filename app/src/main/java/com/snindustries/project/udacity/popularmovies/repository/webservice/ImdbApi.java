package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.snindustries.project.udacity.popularmovies.repository.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public interface ImdbApi {
    @GET("configuration")
    Call<ConfigResponse> getConfiguration();

    @GET("/movie/{movie_id}")
    Call<Movie> getMovie(@Path("movie_id") int movieId);

    @GET("movie/popular")
    Call<MovieSearchResponse> getMoviePopular(@Query("page") int page);

    @GET("movie/top_rated")
    Call<MovieSearchResponse> getMovieTopRated(@Query("page") int page);
}
