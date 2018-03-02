package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * JavaBean class that represents paginated query result witch contains a list of {@link Movie}.
 */

public class PaginatedMoviesResult {

    @Json(name = "results") private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }
}
