package com.snindustries.project.udacity.popularmovies.ui.list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.snindustries.project.udacity.popularmovies.repository.NetworkState;
import com.snindustries.project.udacity.popularmovies.repository.Repository;
import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;

import static com.snindustries.project.udacity.popularmovies.repository.Repository.FAVORITE;
import static com.snindustries.project.udacity.popularmovies.repository.Repository.HIGHEST_RATED;
import static com.snindustries.project.udacity.popularmovies.repository.Repository.MOST_POPULAR;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MoviesViewModel extends AndroidViewModel {


    private final MutableLiveData<PagedList<MovieExt>> movies;
    private final MutableLiveData<NetworkState> networkState;
    private final ObservableInt order;
    private final Repository repository;
    public boolean isLoading = false;
    public long itemPosition = 0;
    public int pageLoaded = 1;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        order = new ObservableInt(MOST_POPULAR);
        order.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                itemPosition = 0;
                pageLoaded = 1;
                getNextMovies();
            }
        });
        networkState = repository.getNetworkState();
        movies = new MutableLiveData<>();
        repository.getFavoriteMovies().observeForever(new Observer<PagedList<MovieExt>>() {
            @Override
            public void onChanged(@Nullable PagedList<MovieExt> movieExts) {
                if (order.get() == FAVORITE) {
                    movies.postValue(movieExts);
                }
            }
        });
        repository.getPopularMovies().observeForever(new Observer<PagedList<MovieExt>>() {
            @Override
            public void onChanged(@Nullable PagedList<MovieExt> movieExts) {
                if (order.get() == MOST_POPULAR) {
                    movies.postValue(movieExts);
                }
            }
        });
        repository.getRatedMovies().observeForever(new Observer<PagedList<MovieExt>>() {
            @Override
            public void onChanged(@Nullable PagedList<MovieExt> movieExts) {
                if (order.get() == HIGHEST_RATED) {
                    movies.postValue(movieExts);
                }
            }
        });
    }

    @NonNull
    private Repository.Strategy getListStrategy() {
        switch (order.get()) {
            case HIGHEST_RATED:
                return Repository.RATING;
            case FAVORITE:
                //fallthrough
            case MOST_POPULAR:
                //fallthrough
            default:
                return Repository.POPULARITY;
        }
    }

    public LiveData<PagedList<MovieExt>> getMovies() {
        return movies;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public void getNextMovies() {
        synchronized (this) {
            if (!isLoading && networkState.getValue() == NetworkState.IDLE || networkState.getValue() == NetworkState.LOADED) {
                isLoading = true;
                repository.get(pageLoaded, itemPosition, new Repository.MovieUpdate() {
                    @Override
                    public void onComplete(int page, long itemsPosition) {
                        pageLoaded = page + 1;
                        itemPosition = itemsPosition;
                        isLoading = false;
                    }
                }, getListStrategy());
            } else {
                switch (order.get()) {
                    case HIGHEST_RATED:
                        movies.postValue(repository.getRatedMovies().getValue());
                        break;
                    case FAVORITE:
                        movies.postValue(repository.getFavoriteMovies().getValue());
                        break;
                    case MOST_POPULAR:
                        movies.postValue(repository.getPopularMovies().getValue());
                        break;
                    default:
                        break;
                }

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
