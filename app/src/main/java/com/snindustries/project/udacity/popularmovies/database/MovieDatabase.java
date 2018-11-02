package com.snindustries.project.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.concurrent.Executors;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase INSTANCE;
    private LiveData<PagedList<Movie>> moviesPaged;

    public static MovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MovieDatabase.class,
                            "movie_db"
                    ).build();
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<PagedList<Movie>> getMoviesPaged() {
        return moviesPaged;
    }

    private void init() {
        moviesPaged = new LivePagedListBuilder<>(
                movieDAO().getAllPaged(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(20)
                        .setPageSize(20)
                        .build())
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();
    }

    public abstract MovieDAO movieDAO();
}
