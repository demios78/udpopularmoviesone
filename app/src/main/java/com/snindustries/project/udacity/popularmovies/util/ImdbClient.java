package com.snindustries.project.udacity.popularmovies.util;

import com.snindustries.project.udacity.popularmovies.BuildConfig;
import com.snindustries.project.udacity.popularmovies.model.ConfigResponse;
import com.snindustries.project.udacity.popularmovies.model.ImageConfig;
import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImdbClient {
    public static final int IMAGE_SIZE = 5;
    private static ImdbClient INSTANCE;
    private final OkHttpClient client;
    private final Params params;


    private ImdbClient() {
        params = new Params();
        client = new OkHttpClient();


        ConfigResponse configurationParams=null;
        try {
            configurationParams = ResponseUtil.parseConfigResponse( getConfiguration() );
        } catch (IOException e) {
            e.printStackTrace();
        }
        params.configResponse = configurationParams;
        params.imageConfig = configurationParams.getImageConfig();

    }


    public static ImdbClient get() {
        if (INSTANCE == null) {
            synchronized (ImdbClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImdbClient();
                }
            }
        }
        return INSTANCE;
    }

    private String getConfiguration() throws IOException {
        Request request = new Request.Builder()
                .url( Params.baseUrl + "/configuration?api_key=" + BuildConfig.ApiKey)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public MovieSearchResponse getMoviesPopular(int page) throws IOException {
        Request request = new Request.Builder()
                .url(Params.baseUrl + "/movie/popular?api_key="+ BuildConfig.ApiKey + "&page="+page)
                .build();
        Response response = client.newCall(request).execute();
        return ResponseUtil.parseMovieSearchResponse( response.body().string() );
    }


    public MovieSearchResponse getMoviesTopRated(int page) throws IOException {
        Request request = new Request.Builder()
                .url(Params.baseUrl + "/movie/top_rated?api_key="+ BuildConfig.ApiKey + "&page="+page)
                .build();
        Response response = client.newCall(request).execute();
        return ResponseUtil.parseMovieSearchResponse( response.body().string() );
    }

    private static class Params{
        ConfigResponse configResponse;
        ImageConfig imageConfig;
        static final String baseUrl = "https://api.themoviedb.org/3";
    }

    public String getPosterURL(Movie movie){
        return params.imageConfig.getBaseUrl() + params.imageConfig.getPosterSizes().get(IMAGE_SIZE) + "/" + movie.getPosterPath() ;
    }

}
