package com.snindustries.project.udacity.popularmovies.repository.database;

import android.arch.persistence.room.Embedded;

import com.snindustries.project.udacity.popularmovies.repository.Movie;

/**
 * Pojo to get both move and its extra properties.
 *
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MovieExt {

    @Embedded
    public ExtraProperties ext;

    @Embedded
    public Movie movie;
}
