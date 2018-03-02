package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * A JavaBean class that represents paginated query result witch contains a list of {@link Review}.
 */

public class PaginatedReviewsResult {

    @Json(name = "results") private List<Review> results;

    public List<Review> getResults() {
        return results;
    }
}
