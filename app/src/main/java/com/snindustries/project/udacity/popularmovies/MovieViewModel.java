package com.snindustries.project.udacity.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import com.snindustries.project.udacity.popularmovies.model.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 10/30/18
 */
public class MovieViewModel extends AndroidViewModel {
    public static final int FAVORITE = 3;
    public static final int HIGHEST_RATED = 1;
    public static final int MOST_POPULAR = 0;
    private LiveData<PagedList<Movie>> moviePagedListRated;
    private MovieRepository movieRepository;
    private ObservableInt sortOrder;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        moviePagedListRated = initRating(application);
        sortOrder = new ObservableInt(MOST_POPULAR);
    }

    public LiveData<PagedList<Movie>> getMoviePagedListRated() {
        return moviePagedListRated;
    }

    public ObservableInt getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder.set(sortOrder);
    }

    private LiveData<PagedList<Movie>> initRating(Application application) {
        movieRepository = MovieRepository.get(application);
        return movieRepository.getMovies();
    }

    public void update(Movie movie, MovieRepository.UpdateCallback callback) {
        movieRepository.update(movie, callback);
    }

}
