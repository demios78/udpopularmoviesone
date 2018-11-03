package com.snindustries.project.udacity.popularmovies.repository.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.Nullable;

import com.snindustries.project.udacity.popularmovies.MovieApplication;
import com.snindustries.project.udacity.popularmovies.repository.Movie;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
@Database(entities = {ExtraProperties.class, Movie.class}, version = 3, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase INSTANCE;
    private Executor executor;
    private LiveData<PagedList<MovieExt>> moviesPaged;

    public static LocalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocalDatabase.class,
                            "movie_db"
                    ).fallbackToDestructiveMigration()
                            .build();
                    INSTANCE.init((MovieApplication) context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<PagedList<MovieExt>> getMoviesPaged() {
        return moviesPaged;
    }

    private void init(MovieApplication context) {
        moviesPaged = new LivePagedListBuilder<>(
                movieDAO().getAllPaged(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(20)
                        .setPageSize(20)
                        .build())
                .setFetchExecutor(context.getDatabaseExe())
                .build();
        executor = context.getDatabaseExe();
    }

    public abstract LocalDao movieDAO();

    public void save(List<MovieExt> entries, @Nullable Callback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long[] success = new long[entries.size()];
                int idx = 0;
                for (MovieExt entry : entries) {
                    movieDAO().save(entry.ext, entry.movie);
                }
                if (callback != null) {
                    callback.onSuccess(success);
                }
            }
        });
    }

    public interface Callback {
        void onSuccess(long[] idsSaved);
    }
}
