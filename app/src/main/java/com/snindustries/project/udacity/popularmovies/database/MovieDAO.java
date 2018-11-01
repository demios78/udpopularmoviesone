package com.snindustries.project.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
@Dao
public interface MovieDAO {
    @Query("select * from movie")
    LiveData<List<Movie>> getAll();

    @Query("SELECT * FROM movie where id=:movieId")
    LiveData<Movie> load(int movieId);

    @Insert(onConflict = REPLACE)
    void save(Movie movie);

    @Query("select * from movie")
    DataSource.Factory<Integer,Movie> getAllPaged();
}
