package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Shaaz Noormohammad
 * (c) 11/4/18
 */
public class ProductionCompany {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("logo_path")
    @Expose
    public Object logoPath;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("origin_country")
    @Expose
    public String originCountry;

}