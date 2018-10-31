package com.snindustries.project.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model for response of Get Movie
 * <p>
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * and
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 *
 * @author shaaz noormohammad
 * (c) October 1, 2018
 */
public class MovieSearchResponse {
    @SerializedName("results")
    public List<Movie> results = null;
    @SerializedName("page")
    private Integer page;
    @SerializedName("total_pages")
    private Integer totalPages;
    @SerializedName("total_results")
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
}
