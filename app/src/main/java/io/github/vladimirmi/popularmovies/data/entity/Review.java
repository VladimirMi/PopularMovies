package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

/**
 * A class that represents a Review model
 */

public class Review {

    @Json(name = "id") private String id;
    @Json(name = "author") private String author;
    @Json(name = "content") private String content;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
