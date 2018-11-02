package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;
import com.snindustries.project.udacity.popularmovies.util.ImdbApi;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MoviePopularWebserviceDataSource extends PageKeyedDataSource<Integer, Movie> {
    private static int INITIAL_PAGE = 1;
    private final MutableLiveData<List<Movie>> movieList;
    private final ImdbApi movieService;
    private final MutableLiveData<NetworkState> networkState;

    public MoviePopularWebserviceDataSource() {
        networkState = new MutableLiveData<>();
        movieService = ImdbClient.get().getApi();
        movieList = new MutableLiveData<>();
    }

    private ImdbApi getMovieService() {
        return movieService;
    }

    public MutableLiveData<List<Movie>> getMovies() {
        return movieList;
    }

    private Call<MovieSearchResponse> getMoviesEndpoint(int page) {
        return getMovieService()
                .getMoviePopular(page);
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        networkState.postValue(NetworkState.LOADING);
        getMoviesEndpoint(params.key)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, (t.getMessage() != null ? t.getMessage() : "unknown error")));
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            Integer key = body.getTotalPages() > params.key ? params.key + 1 : null;
                            callback.onResult(body.getResults(), key);
                            movieList.postValue(body.getResults());
                            networkState.postValue(NetworkState.LOADED);
                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, "Response Body is null."));
                        }

                    }
                });
    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Movie> callback) {
        getMoviesEndpoint(params.key)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, (t.getMessage() != null ? t.getMessage() : "unknown error")));
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        Integer adjacentKey = params.key > 1 ? params.key - 1 : null;
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            callback.onResult(body.getResults(), adjacentKey);
                            movieList.postValue(body.getResults());
                            networkState.postValue(NetworkState.LOADED);
                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, "Response Body is null."));
                        }

                    }
                });
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        getMovieService()
                .getMoviePopular(INITIAL_PAGE)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, (t.getMessage() != null ? t.getMessage() : "unknown error")));
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        MovieSearchResponse body = response.body();
                        if (body != null) {
                            callback.onResult(body.getResults(), body.getPage() - 1, body.getTotalPages() > body.getPage() ? body.getPage() + 1 : body.getPage());
                            movieList.postValue(body.getResults());
                            networkState.postValue(NetworkState.LOADED);
                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, "Response Body is null."));
                        }

                    }
                });
    }
}
