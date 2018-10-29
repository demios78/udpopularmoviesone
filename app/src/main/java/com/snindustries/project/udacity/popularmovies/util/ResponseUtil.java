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

/**
 * Response parsing utilities class to communicate with IMDB APIs.
 *
 * @author shaaz noormohammad
 * October 1, 2018
 */
public final class ResponseUtil {
    private ResponseUtil() {
        //Static access only
    }

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

    private static List<Integer> parseIntArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return new ArrayList<>();
        }
        int arrayLength = jsonArray.length();
        List<Integer> out = new ArrayList<>(arrayLength);
        for (int index = 0; index < arrayLength; index++) {
            out.add(jsonArray.optInt(index));
        }
        return out;
    }

    private static Movie parseMovie(JSONObject json) {
        Movie movie = new Movie();
        movie.setAdult(json.optBoolean("adult"));//fallback to ""
        movie.setBackdropPath(json.optString("backdrop_path"));//fallback to false
        movie.setGenreIds(parseIntArray(json.optJSONArray("genre_ids")));//fallback to 0
        movie.setId(json.optInt("id"));
        movie.setOriginalLanguage(json.optString("original_language"));
        movie.setOriginalTitle(json.optString("original_title"));
        movie.setOverview(json.optString("overview"));
        movie.setPopularity(json.optDouble("popularity"));
        movie.setPosterPath(json.optString("poster_path"));
        movie.setReleaseDate(json.optString("release_date"));
        movie.setTitle(json.optString("title"));
        movie.setVideo(json.optBoolean("video"));
        movie.setVoteAverage(json.optDouble("vote_average"));//fallback to 0d
        movie.setVoteCount(json.optInt("vote_count"));
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
