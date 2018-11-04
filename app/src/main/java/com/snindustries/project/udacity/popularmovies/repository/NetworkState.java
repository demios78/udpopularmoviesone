package com.snindustries.project.udacity.popularmovies.repository;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class NetworkState {
    public static final NetworkState DISCONNECTED = new NetworkState(Status.FAILED, "disconnected");
    public static final NetworkState IDLE = new NetworkState(Status.IDLE, "idle");
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
        IDLE,
        RUNNING,
        SUCCESS,
        FAILED
    }
}
