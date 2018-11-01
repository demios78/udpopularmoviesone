package com.snindustries.project.udacity.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;
import com.snindustries.project.udacity.popularmovies.util.ImdbClient;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Shaaz Noormohammad
 * (c) 10/31/18
 */
public class MovieRepository {
    private static MovieRepository INSTANCE;
    private final Executor executor;
    private final ImdbClient imdbClient = ImdbClient.get();
    private final MovieDAO movieDAO;

    private MovieRepository(Context context) {
        movieDAO = MovieDatabase.getDatabase(context).movieDAO();
        executor = Executors.newSingleThreadExecutor();//TODO move this to an appwide "network" executor
    }

    public static MovieRepository get(Context context) {
        if (INSTANCE == null)
            synchronized (MovieRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieRepository(context);
                }
            }
        return INSTANCE;
    }

    public LiveData<List<Movie>> getPopularMovieList(Integer page) {
        final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        imdbClient.getApi().getMoviePopular(page)
                .enqueue(new Callback<MovieSearchResponse>() {
                    @Override
                    public void onFailure(Call<MovieSearchResponse> call, Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                        if (response.body() != null) {
                            data.setValue(response.body().getResults());
                        } else {
                            Log.e("MovieSearchResponse", "Response body is null");
                        }
                    }
                });
        return data;
    }
}
