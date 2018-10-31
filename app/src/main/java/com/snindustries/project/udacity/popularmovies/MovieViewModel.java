package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;

import com.snindustries.project.udacity.popularmovies.model.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MovieViewModel extends ViewModel {
    LiveData<PageKeyedDataSource<Integer, Movie>> liveDataSource;
    LiveData<PagedList<Movie>> moviePagedList;

    public MovieViewModel() {
        MovieDatasourceFactory factory = new MovieDatasourceFactory();

        liveDataSource = factory.getMovieLiveDataSource();

        PagedList.Config pConfig = new PagedList.Config.Builder()
                .setPageSize(20)//TODO: move this to config?
                .setEnablePlaceholders(false)
                .build();
        moviePagedList = new LivePagedListBuilder<>(factory, pConfig).build();
    }
}
