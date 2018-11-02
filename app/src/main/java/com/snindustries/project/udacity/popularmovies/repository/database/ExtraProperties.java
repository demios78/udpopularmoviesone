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

    @ColumnInfo(name = "favorite")
    public boolean favorite;
    @PrimaryKey
    @ColumnInfo(name = "ex_id")
    public long id;
    @ColumnInfo(name = "popularity")
    public long popularityOrder;
    @ColumnInfo(name = "rating")
    public long ratingOrder;
    @ColumnInfo(name = "time_updated")
    public long time_updated;

}
