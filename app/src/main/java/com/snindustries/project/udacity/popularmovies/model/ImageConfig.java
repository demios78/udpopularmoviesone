package com.snindustries.project.udacity.popularmovies.model;

import java.util.List;

class ImageConfig {

    private List<String> backdropSizes = null;
    private String baseUrl;
    private List<String> logoSizes = null;
    private List<String> posterSizes = null;
    private List<String> profileSizes = null;
    private String secureBaseUrl;
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
