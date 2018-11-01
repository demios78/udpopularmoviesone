package com.snindustries.project.udacity.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author Shaaz Noormohammad
 * (c) 10/31/18
 */
public class MovieTypeConverter {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String intListToString(List<Integer> data) {
        return gson.toJson(data);
    }

    @TypeConverter
    public static List<Integer> stringToIntList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }
}
