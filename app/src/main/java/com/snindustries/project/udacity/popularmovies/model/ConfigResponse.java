package com.snindustries.project.udacity.popularmovies.model;

import java.util.List;


public class ConfigResponse {

    private List<String> changeKeys = null;
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
