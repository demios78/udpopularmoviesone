package com.snindustries.project.udacity.popularmovies.repository.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
@Entity(tableName = "extra")
public class ExtraProperties {

    @ColumnInfo(name = "ex_favorite")
    public boolean favorite;
    @PrimaryKey
    @ColumnInfo(name = "ex_id")
    public long id;
    @ColumnInfo(name = "ex_popularity")
    public long popularityOrder;
    @ColumnInfo(name = "ex_rating")
    public long ratingOrder;
    @ColumnInfo(name = "ex_time_updated")
    public long time_updated;

}
