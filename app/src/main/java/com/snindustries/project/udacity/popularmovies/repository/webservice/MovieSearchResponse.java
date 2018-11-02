package com.snindustries.project.udacity.popularmovies.repository.webservice;

import com.snindustries.project.udacity.popularmovies.repository.Movie;

import java.util.List;

/**
 *
 * Model for response of Get Movie
 *
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * and
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 *
 * @author shaaz noormohammad
 * (c) October 1, 2018
 */
public class MovieSearchResponse {
    private Integer page;
    private Integer totalResults;
    private Integer totalPages;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public List<Movie> results = null;
}
