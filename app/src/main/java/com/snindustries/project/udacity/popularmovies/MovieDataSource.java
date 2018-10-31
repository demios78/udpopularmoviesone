package com.snindustries.project.udacity.popularmovies;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {
    private static int INITIAL_PAGE=1;

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        ImdbClient.get().getApi()
                .getMoviePopular(params.key)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            Integer key = body.getTotalPages() > params.key ? params.key + 1 : null;
                            callback.onResult(body.getResults(), key);
                        }

                    }
                });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        ImdbClient.get().getApi()
                .getMoviePopular(params.key)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        Integer adjacentKey = params.key > 1 ? params.key - 1 : null;
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            callback.onResult(body.getResults(), adjacentKey);
                        }
                    }
                });
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        ImdbClient.get().getApi()
                .getMoviePopular(INITIAL_PAGE)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            callback.onResult(body.getResults(), body.getPage() - 1, body.getTotalPages() > body.getPage() ? body.getPage() + 1 : body.getPage());
                        }
                    }
                });
    }
}
