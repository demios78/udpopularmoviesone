package com.snindustries.project.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.util.List;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
@Dao
public interface MovieDAO {
    @Query("select * from movie")
    LiveData<List<Movie>> getAll();

    @Query("select * from movie")
    DataSource.Factory<Integer, Movie> getAllPaged();

    @Query("SELECT * FROM movie where id=:movieId")
    LiveData<Movie> load(int movieId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] save(Movie... movie);

    @Update
    int update(Movie... movie);
}
