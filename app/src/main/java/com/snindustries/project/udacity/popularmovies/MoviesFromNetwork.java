package com.snindustries.project.udacity.popularmovies;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
public class MoviesFromNetwork {

    private final LiveData<PagedList<Movie>> moviesPaged;
    private final LiveData<List<Movie>> networkLoadedMovies;
    private final LiveData<NetworkState> networkState;

    public MoviesFromNetwork(MoviePopularDataSourceFactory factory, PagedList.BoundaryCallback<Movie> boundaryCallback) {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .build();

        networkState = Transformations.switchMap(factory.getDataSource(), new Function<MoviePopularWebserviceDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(MoviePopularWebserviceDataSource input) {
                return input.getNetworkState();
            }
        });
        networkLoadedMovies = Transformations.switchMap(factory.getDataSource(), new Function<MoviePopularWebserviceDataSource, LiveData<List<Movie>>>() {
            @Override
            public LiveData<List<Movie>> apply(MoviePopularWebserviceDataSource input) {
                return input.getMovies();
            }
        });


        Executor executor = Executors.newFixedThreadPool(3);
        LivePagedListBuilder builder = new LivePagedListBuilder<>(factory, pagedListConfig);
        moviesPaged = builder
                .setFetchExecutor(executor)
                .setBoundaryCallback(boundaryCallback)
                .build();

    }

    public LiveData<PagedList<Movie>> getMoviesPaged() {
        return moviesPaged;
    }

    public LiveData<List<Movie>> getNetworkLoadedMovies() {
        return networkLoadedMovies;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
}
