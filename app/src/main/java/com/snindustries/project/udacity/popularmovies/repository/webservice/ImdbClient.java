package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snindustries.project.udacity.popularmovies.BuildConfig;
import com.snindustries.project.udacity.popularmovies.repository.Movie;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private static final String defaultParams = "{\"images\":{\"base_url\":\"http://image.tmdb.org/t/p/\",\"secure_base_url\":\"https://image.tmdb.org/t/p/\",\"backdrop_sizes\":[\"w300\",\"w780\",\"w1280\",\"original\"],\"logo_sizes\":[\"w45\",\"w92\",\"w154\",\"w185\",\"w300\",\"w500\",\"original\"],\"poster_sizes\":[\"w92\",\"w154\",\"w185\",\"w342\",\"w500\",\"w780\",\"original\"],\"profile_sizes\":[\"w45\",\"w185\",\"h632\",\"original\"],\"still_sizes\":[\"w92\",\"w185\",\"w300\",\"original\"]},\"change_keys\":[\"adult\",\"air_date\",\"also_known_as\",\"alternative_titles\",\"biography\",\"birthday\",\"budget\",\"cast\",\"certifications\",\"character_names\",\"created_by\",\"crew\",\"deathday\",\"episode\",\"episode_number\",\"episode_run_time\",\"freebase_id\",\"freebase_mid\",\"general\",\"genres\",\"guest_stars\",\"homepage\",\"images\",\"imdb_id\",\"languages\",\"name\",\"network\",\"origin_country\",\"original_name\",\"original_title\",\"overview\",\"parts\",\"place_of_birth\",\"plot_keywords\",\"production_code\",\"production_companies\",\"production_countries\",\"releases\",\"revenue\",\"runtime\",\"season\",\"season_number\",\"season_regular\",\"spoken_languages\",\"status\",\"tagline\",\"title\",\"translations\",\"tvdb_id\",\"tvrage_id\",\"type\",\"video\",\"videos\"]}";
    private static ImdbClient INSTANCE;
    private final ImdbApi api;
    private final Params params;
    private Retrofit retrofit;

    private ImdbClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        params = new Params();
        params.configResponse = gson.fromJson(defaultParams, ConfigResponse.class);
        params.imageConfig = params.configResponse.getImageConfig();

        OkHttpClient client = new OkHttpClient.Builder()
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
                //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Params.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        api = createApi();
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

    public static ImdbApi getApi() {
        return get().api;
    }

    private ImdbApi createApi() {
        return retrofit.create(ImdbApi.class);
    }

    public String getPosterURL(Movie movie) {
        return params.imageConfig.getBaseUrl() + params.imageConfig.getPosterSizes().get(IMAGE_SIZE) + "/" + movie.getPosterPath();
    }

    public String getPosterURL(String posterPath) {
        return params.imageConfig.getBaseUrl() + params.imageConfig.getPosterSizes().get(IMAGE_SIZE) + "/" + posterPath;
    }

    private void initializeConfiguration() {
        api.getConfiguration().enqueue(new Callback<ConfigResponse>() {
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
