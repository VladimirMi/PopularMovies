package io.github.vladimirmi.popularmovies.data.entity;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import io.github.vladimirmi.popularmovies.data.net.Api;

/**
 * A class that represents a Video model.
 */

public class Video {

    @Json(name = "id") private String id;
    @Json(name = "key") private String key;
    @Json(name = "name") private String name;

    public Video(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public String getUrl() {
        return String.format(Api.YOUTUBE_VIDEO_URL, key);
    }

    @Nullable
    public String getThumbnailUrl() {
        return String.format(Api.YOUTUBE_THUMBNAIL_URL, key);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

}
