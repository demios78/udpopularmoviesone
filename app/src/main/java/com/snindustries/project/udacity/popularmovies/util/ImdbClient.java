package com.snindustries.project.udacity.popularmovies.util;

import android.app.Application;

import com.snindustries.project.udacity.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImdbClient {
    private static ImdbClient INSTANCE;
    private final OkHttpClient client;

    private ImdbClient(Application application) {
        client = new OkHttpClient();
        try {
            String config = getConfiguration();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ImdbClient get(Application application) {
        if (INSTANCE == null) {
            synchronized (ImdbClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImdbClient(application);
                }
            }
        }
        return INSTANCE;
    }

    public String getConfiguration() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/configuration?api_key=" + BuildConfig.ApiKey)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}
