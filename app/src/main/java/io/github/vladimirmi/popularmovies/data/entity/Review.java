package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

/**
 * A JavaBean class that represents a JSON Review model
 * <pre>
 *  {
 *  "id": "557693fac3a368569a003fab",
 *  "author": "Author name",
 *  "content": "Excellent movie.",
 *  "url": "https://www.themoviedb.org/review/557693fac3a368569a003fab"
 *  }
 * </pre>
 */

public class Review {

    @Json(name = "id") private String id;
    @Json(name = "author") private String author;
    @Json(name = "content") private String content;
    @Json(name = "url") private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
