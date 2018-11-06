package com.snindustries.project.udacity.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Application scope items.
 *
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class MovieApplication extends Application {
    public static final int N_THREADS = 3;
    protected Executor database;

    public Executor getDatabaseExe() {
        return database;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        database = Executors.newFixedThreadPool(N_THREADS);
    }

}
