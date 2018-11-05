package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Shaaz Noormohammad
 * (c) 11/4/18
 */
public class Genre {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;

}