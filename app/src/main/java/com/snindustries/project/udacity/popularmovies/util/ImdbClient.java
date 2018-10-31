package com.snindustries.project.udacity.popularmovies.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snindustries.project.udacity.popularmovies.BuildConfig;
import com.snindustries.project.udacity.popularmovies.model.ConfigResponse;
import com.snindustries.project.udacity.popularmovies.model.ImageConfig;
import com.snindustries.project.udacity.popularmovies.model.Movie;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class to communicate with IMDB APIs.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public class ImdbClient {
    private static final int IMAGE_SIZE = 5;
    private static ImdbClient INSTANCE;
    private final OkHttpClient client;
    private final Params params;
    private Retrofit retrofit;


    private ImdbClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        params = new Params();

        client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("api_key", BuildConfig.ApiKey)
                                .build();
                        return chain.proceed(request.newBuilder().url(url).build());
                    }
                })
                .addInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Params.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();


        initializeConfiguration();
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

    public ImdbApi getApi() {
        return retrofit.create(ImdbApi.class);
    }

    public String getPosterURL(Movie movie) {
        return params.imageConfig.getBaseUrl() + params.imageConfig.getPosterSizes().get(IMAGE_SIZE) + "/" + movie.getPosterPath();
    }

    private void initializeConfiguration() {
        getApi().getConfiguration().enqueue(new Callback<ConfigResponse>() {
            @Override
            public void onFailure(Call<ConfigResponse> call, Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
                if (response.body() != null) {
                    params.configResponse = response.body();
                    params.imageConfig = response.body().getImageConfig();
                }
            }
        });
    }

    private static class Params {
        static final String baseUrl = "https://api.themoviedb.org/3/";
        ConfigResponse configResponse;
        ImageConfig imageConfig;
    }

}
