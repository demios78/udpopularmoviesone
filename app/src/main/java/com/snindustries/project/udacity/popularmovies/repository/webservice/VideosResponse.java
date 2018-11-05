package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Shaaz Noormohammad
 * (c) 11/4/18
 */
public class VideosResponse {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<Video> results = null;
}