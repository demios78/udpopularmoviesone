package com.snindustries.project.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model for response of Get API configuration
 * <p>
 * https://developers.themoviedb.org/3/configuration/get-api-configuration
 *
 * @author shaaz noormohammad
 * (c) October 1, 2018
 */
public class ImageConfig {

    @SerializedName("backdrop_sizes")
    private List<String> backdropSizes = null;
    @SerializedName("base_url")
    private String baseUrl;
    @SerializedName("logo_sizes")
    private List<String> logoSizes = null;
    @SerializedName("poster_sizes")
    private List<String> posterSizes = null;
    @SerializedName("profile_sizes")
    private List<String> profileSizes = null;
    @SerializedName("secure_base_url")
    private String secureBaseUrl;
    @SerializedName("still_sizes")
    private List<String> stillSizes = null;

    public List<String> getBackdropSizes() {
        return backdropSizes;
    }

    public void setBackdropSizes(List<String> backdropSizes) {
        this.backdropSizes = backdropSizes;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getLogoSizes() {
        return logoSizes;
    }

    public void setLogoSizes(List<String> logoSizes) {
        this.logoSizes = logoSizes;
    }

    public List<String> getPosterSizes() {
        return posterSizes;
    }

    public void setPosterSizes(List<String> posterSizes) {
        this.posterSizes = posterSizes;
    }

    public List<String> getProfileSizes() {
        return profileSizes;
    }

    public void setProfileSizes(List<String> profileSizes) {
        this.profileSizes = profileSizes;
    }

    public String getSecureBaseUrl() {
        return secureBaseUrl;
    }

    public void setSecureBaseUrl(String secureBaseUrl) {
        this.secureBaseUrl = secureBaseUrl;
    }

    public List<String> getStillSizes() {
        return stillSizes;
    }

    public void setStillSizes(List<String> stillSizes) {
        this.stillSizes = stillSizes;
    }
}
