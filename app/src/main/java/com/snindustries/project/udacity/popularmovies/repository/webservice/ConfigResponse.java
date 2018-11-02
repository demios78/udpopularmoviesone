package com.snindustries.project.udacity.popularmovies.repository.webservice;

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
public class ConfigResponse {

    @SerializedName("change_keys")
    private List<String> changeKeys = null;
    @SerializedName("images")
    private ImageConfig imageConfig;

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

    public ImageConfig getImageConfig() {
        return imageConfig;
    }

    public void setImageConfig(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;
    }
}
