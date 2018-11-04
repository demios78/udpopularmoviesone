package com.snindustries.project.udacity.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.content.Context;

import com.snindustries.project.udacity.popularmovies.MovieApplication;
import com.snindustries.project.udacity.popularmovies.repository.database.ExtraProperties;
import com.snindustries.project.udacity.popularmovies.repository.database.LocalDatabase;
import com.snindustries.project.udacity.popularmovies.repository.database.MovieExt;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbApi;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.snindustries.project.udacity.popularmovies.repository.webservice.MovieSearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Shaaz Noormohammad
 * (c) 11/2/18
 */
public class Repository {

    public static final int FAVORITE = 3;
    public static final int HIGHEST_RATED = 1;
    public static final int MOST_POPULAR = 0;

    public static final Strategy POPULARITY = new PopularityStrategy();
    public static final Strategy RATING = new RatingStrategy();
    private final LocalDatabase database;
    private final ImdbApi network;
    private final Executor networkExecutor;
    private final MutableLiveData<NetworkState> networkState;

    public Repository(Context context) {
        network = ImdbClient.getApi();
        database = LocalDatabase.getDatabase(context);
        networkState = new MutableLiveData<>();
        networkExecutor = ((MovieApplication) context.getApplicationContext()).getNetworkExe();
    }

    public void get(int page, long itemPosition, MovieUpdate updateCallback, Strategy strategy) {
        networkState.postValue(NetworkState.LOADING);
        strategy.getMovies(network, page)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, (t.getMessage() != null ? t.getMessage() : "unknown error")));
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        MovieSearchResponse body = response.body();
                        if (body != null && body.getResults() != null) {

                            networkExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    saveResponse(body, itemPosition, updateCallback, page, strategy);
                                }
                            });

                            networkState.postValue(NetworkState.LOADED);
                        } else {
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, "Response Body is null."));
                        }
                    }
                });
    }

    public LiveData<PagedList<MovieExt>> getFavoriteMovies() {
        return database.getMoviesPagedFavorite();
    }

    public LiveData<PagedList<MovieExt>> getPopularMovies() {
        return database.getMoviesPagedPopular();
    }

    public LiveData<PagedList<MovieExt>> getRatedMovies() {
        return database.getMoviesPagedRated();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    private void saveResponse(MovieSearchResponse body, long itemPosition, MovieUpdate updateCallback, int page, Strategy strategy) {
        List<MovieExt> movieExts = new ArrayList<>(body.getResults().size());

        for (int i = 0; i < body.getResults().size(); i++) {
            Movie movie = body.getResults().get(i);
            ExtraProperties ext;// = new ExtraProperties();

            ext = database.getExtraProperties(movie.getId());

            if (ext == null) {
                ext = new ExtraProperties();
            }

            ext.id = movie.getId();
            ext.time_updated = System.currentTimeMillis();
            strategy.updateOrder(itemPosition, i, ext);

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
    }


    public void update(ExtraProperties ext) {
        if (ext.id != 0) {
            database.update(ext);
        }
    }

    public interface MovieUpdate {
        void onComplete(int page, long itemsPosition);
    }

    public interface Strategy {
        Call<MovieSearchResponse> getMovies(ImdbApi network, int page);

        void updateOrder(long itemPosition, int i, ExtraProperties ext);
    }

    private static class PopularityStrategy implements Strategy {
        private PopularityStrategy() {
            //Do not instantiate
        }

        @Override
        public Call<MovieSearchResponse> getMovies(ImdbApi network, int page) {
            return network.getMoviePopular(page);
        }

        @Override
        public void updateOrder(long itemPosition, int i, ExtraProperties ext) {
            ext.popularityOrder = itemPosition + i + 1;
        }
    }

    private static class RatingStrategy implements Strategy {
        private RatingStrategy() {
            //Do not instantiate
        }

        @Override
        public Call<MovieSearchResponse> getMovies(ImdbApi network, int page) {
            return network.getMovieTopRated(page);
        }

        @Override
        public void updateOrder(long itemPosition, int i, ExtraProperties ext) {
            ext.ratingOrder = itemPosition + i + 1;
        }
    }


}
