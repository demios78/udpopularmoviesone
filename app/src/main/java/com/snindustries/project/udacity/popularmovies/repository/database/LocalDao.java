package com.snindustries.project.udacity.popularmovies.repository.database;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.snindustries.project.udacity.popularmovies.repository.Movie;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
@Dao
public interface LocalDao {

    @Query("select * from movies inner join extra on movies.id = extra.ex_id and ex_favorite='1' order by ex_popularity asc")
    DataSource.Factory<Integer, MovieExt> getAllFavoritePaged();

    @Query("select * from movies inner join extra on movies.id = extra.ex_id and ex_popularity != " + Integer.MAX_VALUE + " order by ex_popularity asc")
    DataSource.Factory<Integer, MovieExt> getAllPopularPaged();

    @Query("select * from movies inner join extra on movies.id = extra.ex_id and ex_rating != " + Integer.MAX_VALUE + "  order by ex_rating asc")
    DataSource.Factory<Integer, MovieExt> getAllRatedPaged();


    @Query("select * from extra where ex_id=:id")
    ExtraProperties getExtraProperties(Integer id);

    @Query("select * from movies inner join extra on movies.id = extra.ex_id and id=:movieId")
    LiveData<MovieExt> getMovie(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(ExtraProperties ext, Movie movie);

    @Update
    void update(ExtraProperties ext);
}
