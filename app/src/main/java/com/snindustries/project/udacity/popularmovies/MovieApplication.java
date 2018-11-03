package com.snindustries.project.udacity.popularmovies;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MovieApplication extends Application {
    public static final int N_THREADS = 1;
    Executor database;
    Executor network;
    Executor worker;

    public Executor getDatabaseExe() {
        return database;
    }

    public Executor getNetworkExe() {
        return network;
    }

    public Executor getWorkerExe() {
        return worker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        network = Executors.newFixedThreadPool(N_THREADS);
        database = Executors.newFixedThreadPool(N_THREADS);
        worker = Executors.newFixedThreadPool(N_THREADS);
    }

}
