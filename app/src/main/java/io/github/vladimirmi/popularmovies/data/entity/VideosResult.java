package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * JavaBean class that represents query result witch contains a list of {@link Video}.
 */

public class VideosResult {

    @Json(name = "results") private List<Video> results;

    public List<Video> getResults() {
        return results;
    }
}
