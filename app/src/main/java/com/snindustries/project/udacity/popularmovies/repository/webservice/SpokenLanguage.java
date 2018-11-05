package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Shaaz Noormohammad
 * (c) 11/4/18
 */
public class SpokenLanguage {

    @SerializedName("iso_639_1")
    @Expose
    public String iso6391;
    @SerializedName("name")
    @Expose
    public String name;

}