package com.snindustries.project.udacity.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.content.Context;

import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.LocalDatabase;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbApi;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.snindustries.project.udacity.popularmovies.repository.webservice.MovieSearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class Repository {

    private final LocalDatabase database;
    private final LiveData<PagedList<MovieExt>> movies;
    private final ImdbApi network;
    private final MutableLiveData<NetworkState> networkState;

    public Repository(Context context) {
        network = ImdbClient.getApi();
        database = LocalDatabase.getDatabase(context);
        movies = database.getMoviesPaged();
        networkState = new MutableLiveData<>();
    }

    public void get(int page, long itemPosition, MovieUpdate updateCallback) {
        networkState.postValue(NetworkState.LOADING);
        network.getMoviePopular(page).enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, (t.getMessage() != null ? t.getMessage() : "unknown error")));
            }

            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                MovieSearchResponse body = response.body();
                if (body != null && body.getResults() != null) {

                    List<MovieExt> movieExts = new ArrayList<>(body.getResults().size());

                    for (int i = 0; i < body.getResults().size(); i++) {
                        Movie movie = body.getResults().get(i);
                        ExtraProperties ext = new ExtraProperties();
                        ext.id = movie.getId();
                        ext.favorite = false;//TODO get old fav
                        ext.popularityOrder = 0;//TODO get old pop order
                        ext.ratingOrder = 0;//TODO get old rating order
                        ext.time_updated = System.currentTimeMillis();
                        ext.popularityOrder = itemPosition + i + 1;

                        MovieExt movieExt = new MovieExt();
                        movieExt.movie = movie;
                        movieExt.ext = ext;

                        movieExts.add(movieExt);
                    }

                    database.save(movieExts, new LocalDatabase.Callback() {
                        @Override
                        public void onSuccess(long[] idsSaved) {
                            if (updateCallback != null && idsSaved.length > 0) {
                                updateCallback.onComplete(page, itemPosition + idsSaved.length);
                            }
                        }
                    });
                    networkState.postValue(NetworkState.LOADED);
                } else {
                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, "Response Body is null."));
                }
            }
        });
    }


    public LiveData<PagedList<MovieExt>> getMovies() {
        return movies;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public interface MovieUpdate {
        void onComplete(int page, long itemsPosition);
    }
}
