package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.snindustries.project.udacity.popularmovies.model.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MoviePopularDataSourceFactory extends DataSource.Factory<Integer, Movie> {

    private MutableLiveData<MoviePopularWebserviceDataSource> dataSourceMutableLiveData = new MutableLiveData<>();
    private MoviePopularWebserviceDataSource movieDataSource;

    @Override
    public DataSource<Integer, Movie> create() {
        movieDataSource = new MoviePopularWebserviceDataSource();
        dataSourceMutableLiveData.postValue(movieDataSource);
        return movieDataSource;
    }

    public MutableLiveData<MoviePopularWebserviceDataSource> getDataSource() {
        return dataSourceMutableLiveData;
    }

}
