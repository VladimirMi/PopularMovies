package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * JavaBean class that represents paginated query result witch contains a list of {@link Movie}.
 */

public class PaginatedMoviesResult {

    @Json(name = "page") private int page;
    @Json(name = "total_results") private int totalResults;
    @Json(name = "total_pages") private int totalPages;
    @Json(name = "results") private List<Movie> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
