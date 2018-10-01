package com.snindustries.project.udacity.popularmovies.util;

import com.snindustries.project.udacity.popularmovies.model.ConfigResponse;
import com.snindustries.project.udacity.popularmovies.model.ImageConfig;
import com.snindustries.project.udacity.popularmovies.model.Movie;
import com.snindustries.project.udacity.popularmovies.model.MovieSearchResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseUtil {
    public static ConfigResponse parseConfigResponse(String input) {
        JSONObject jsonObject;
        ConfigResponse configResponse = new ConfigResponse();

        try {
            jsonObject = new JSONObject(input);
            configResponse.setImageConfig(parseImageConfig(jsonObject.getJSONObject("images")));
             configResponse.setChangeKeys(parseStringArray(jsonObject.getJSONArray("change_keys")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return configResponse;
    }

    private static ImageConfig parseImageConfig(JSONObject json) throws JSONException {
        ImageConfig out = new ImageConfig();
        out.setBaseUrl(json.getString("base_url"));
        out.setSecureBaseUrl(json.getString("secure_base_url"));
        out.setBackdropSizes(parseStringArray(json.getJSONArray("backdrop_sizes")));
        out.setLogoSizes(parseStringArray(json.getJSONArray("logo_sizes")));
        out.setPosterSizes(parseStringArray(json.getJSONArray("poster_sizes")));
        out.setProfileSizes(parseStringArray(json.getJSONArray("profile_sizes")));
        out.setStillSizes(parseStringArray(json.getJSONArray("still_sizes")));

        return out;
    }

    private static List<Integer> parseIntArray(JSONArray jsonArray) throws JSONException {
        int arrayLength = jsonArray.length();
        List<Integer> out = new ArrayList<>(arrayLength);
        for (int index = 0; index < arrayLength; index++) {
            out.add(jsonArray.getInt(index));
        }
        return out;
    }

    private static Movie parseMovie(JSONObject json) throws JSONException {
        Movie movie = new Movie();
        movie.setAdult(json.getBoolean("adult"));
        movie.setBackdropPath(json.getString("backdrop_path"));
        movie.setGenreIds(parseIntArray(json.getJSONArray("genre_ids")));
        movie.setId(json.getInt("id"));
        movie.setOriginalLanguage(json.getString("original_language"));
        movie.setOriginalTitle(json.getString("original_title"));
        movie.setOverview(json.getString("overview"));
        movie.setPopularity(json.getDouble("popularity"));
        movie.setPosterPath(json.getString("poster_path"));
        movie.setReleaseDate(json.getString("release_date"));
        movie.setTitle(json.getString("title"));
        movie.setVideo(json.getBoolean("video"));
        movie.setVoteAverage(json.getDouble("vote_average"));
        movie.setVoteCount(json.getInt("vote_count"));
        return movie;
    }

    public static MovieSearchResponse parseMovieSearchResponse(String input) {
        MovieSearchResponse response = new MovieSearchResponse();
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(input);
            response.setPage(jsonObject.getInt("page"));
            response.setTotalPages(jsonObject.getInt("total_pages"));
            response.setTotalResults(jsonObject.getInt("total_results"));
            response.setResults(parseMovies(jsonObject.getJSONArray("results")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static List<Movie> parseMovies(JSONArray results) throws JSONException {
        List<Movie> out = new ArrayList<>(results.length());
        for (int index = 0; index < results.length(); index++) {
            out.add(parseMovie(results.getJSONObject(index)));
        }
        return out;
    }

    private static List<String> parseStringArray(JSONArray jsonArray) throws JSONException {
        int arrayLength = jsonArray.length();
        List<String> out = new ArrayList<>(arrayLength);
        for (int index = 0; index < arrayLength; index++) {
            out.add(jsonArray.getString(index));
        }
        return out;
    }
}
