package com.snindustries.project.udacity.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.snindustries.project.udacity.popularmovies.model.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static MovieDatabase INSTANCE;

    public static MovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MovieDatabase.class,
                            "movie_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract MovieDAO movieDAO();
}
