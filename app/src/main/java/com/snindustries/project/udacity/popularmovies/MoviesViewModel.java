package com.snindustries.project.udacity.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.snindustries.project.udacity.popularmovies.repository.NetworkState;
import com.snindustries.project.udacity.popularmovies.repository.Repository;
import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MoviesViewModel extends AndroidViewModel {

    public static final int FAVORITE = 3;
    public static final int HIGHEST_RATED = 1;
    public static final int MOST_POPULAR = 0;

    private final LiveData<PagedList<MovieExt>> movies;
    private final MutableLiveData<NetworkState> networkState;
    private final ObservableInt order;
    private final Repository repository;
    public boolean isLoading = false;
    public long itemPosition = 0;
    public int pageLoaded = 1;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        movies = repository.getMovies();
        order = new ObservableInt(MOST_POPULAR);
        order.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                itemPosition = 0;
                pageLoaded = 1;
                getNextPopularMovies();
            }
        });
        networkState = repository.getNetworkState();
    }

    @NonNull
    private Repository.Strategy getListStrategy() {
        switch (order.get()) {
            case HIGHEST_RATED:
                return Repository.RATING;
            case MOST_POPULAR:
                //fallthrough
            default:
                return Repository.POPULARITY;
        }
    }

    public LiveData<PagedList<MovieExt>> getMovies() {
        return movies;
    }

    public void getNextPopularMovies() {
        synchronized (this) {
            if (!isLoading) {
                isLoading = true;
                repository.get(pageLoaded, itemPosition, new Repository.MovieUpdate() {
                    @Override
                    public void onComplete(int page, long itemsPosition) {
                        pageLoaded = page + 1;
                        itemPosition = itemsPosition;
                        isLoading = false;
                    }
                }, getListStrategy());
            }
        }
    }

    public ObservableInt getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order.set(order);
    }

    public void toggleFavorite(ExtraProperties ext) {
        ext.favorite = !ext.favorite;
        repository.update(ext);
    }
}
