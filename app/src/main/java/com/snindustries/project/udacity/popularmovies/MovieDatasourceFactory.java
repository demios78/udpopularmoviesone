package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PageKeyedDataSource;

import com.snindustries.project.udacity.popularmovies.model.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MovieDatasourceFactory extends DataSource.Factory<Integer, Movie> {

    private MutableLiveData<PageKeyedDataSource<Integer, Movie>> movieLiveDataSource = new MutableLiveData<>();

    @Override
    public DataSource<Integer, Movie> create() {
        MovieWebserviceDataSource movieDataSource = new MovieWebserviceDataSource();
        movieLiveDataSource.postValue(movieDataSource); //Why are we caching?
        return movieDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Movie>> getMovieLiveDataSource() {
        return movieLiveDataSource;
    }
}
