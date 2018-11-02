package com.snindustries.project.udacity.popularmovies;

/**
 * @author Shaaz Noormohammad
 * (c) 11/1/18
 */
class NetworkState {
    public static final NetworkState LOADED = new NetworkState(Status.SUCCESS, "Success");
    public static final NetworkState LOADING = new NetworkState(Status.RUNNING, "Running");
    private final String msg;
    private final Status status;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }
}
