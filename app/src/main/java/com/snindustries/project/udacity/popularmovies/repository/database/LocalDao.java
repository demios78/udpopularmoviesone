package com.snindustries.project.udacity.popularmovies.repository.database;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.snindustries.project.udacity.popularmovies.repository.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
@Dao
public interface LocalDao {

    @Query("select * from movies inner join extra on movies.id = extra.ex_id order by popularity desc")
    DataSource.Factory<Integer, MovieExt> getAllPaged();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ExtraProperties ext, Movie movie);
}