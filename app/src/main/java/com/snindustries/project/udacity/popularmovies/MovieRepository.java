package com.snindustries.project.udacity.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snindustries.project.udacity.popularmovies.database.MovieDatabase;
import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Shaaz Noormohammad
 * (c) 10/31/18
 */
public class MovieRepository {
    private static MovieRepository INSTANCE;
    private final ExecutorService dbExecutor;
    private final Handler handler;
    private final MediatorLiveData<PagedList<Movie>> liveDataMerger;
    private final MovieDatabase movieDatabase;
    private final MoviesFromNetwork movieFromNetwork;
    private PagedList.BoundaryCallback<Movie> boundaryCallback =
            new PagedList.BoundaryCallback<Movie>() {
                @Override
                public void onZeroItemsLoaded() {
                    super.onZeroItemsLoaded();
                    liveDataMerger.addSource(movieDatabase.getMoviesPaged(),
                            new Observer<PagedList<Movie>>() {
                                @Override
                                public void onChanged(@Nullable PagedList<Movie> movies) {
                                    liveDataMerger.setValue(movies);
                                }
                            });
                }
            };

    private MovieRepository(Context context) {
        movieDatabase = MovieDatabase.getDatabase(context);
        dbExecutor = Executors.newSingleThreadExecutor();
        handler = new Handler(context.getMainLooper());

        movieFromNetwork = new MoviesFromNetwork(new MoviePopularDataSourceFactory(), boundaryCallback);

        liveDataMerger = new MediatorLiveData<>();
        liveDataMerger.addSource(movieFromNetwork.getMoviesPaged(), liveDataMerger::setValue);

        movieFromNetwork.getNetworkLoadedMovies().observeForever(new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null && !movies.isEmpty()) {
                    dbExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            for (Movie movie : movies) {

                                //movieDatabase.movieDAO().save(movie);

                            }
                            Log.d(this.getClass().getSimpleName(), "Saved movise  " + movies.size());
                        }
                    });
                }
            }
        });

    }

    public static MovieRepository get(Context context) {
        if (INSTANCE == null)
            synchronized (MovieRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieRepository(context);
                }
            }
        return INSTANCE;
    }

    public MediatorLiveData getMovies() {
        return liveDataMerger;
    }

    public LiveData<NetworkState> getNetworkState() {
        return movieFromNetwork.getNetworkState();
    }

    public void update(Movie movie, UpdateCallback callback) {
        dbExecutor.submit(new Runnable() {
            @Override
            public void run() {
                int rows = movieDatabase.movieDAO().update(movie);
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.updated(rows);
                        }
                    });
                }
            }
        });
    }

    interface UpdateCallback {
        void updated(int row);
    }
}
