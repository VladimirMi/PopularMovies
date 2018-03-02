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
    @Json(name = "site") private String site;

    public String getId() {
        return id;
    }

    @Nullable
    public String getUrl() {
        if (checkIsYouTube()) {
            return String.format(Api.YOUTUBE_VIDEO_URL, key);
        } else {
            return null;
        }
    }

    @Nullable
    public String getThumbnailUrl() {
        if (checkIsYouTube()) {
            return String.format(Api.YOUTUBE_THUMBNAIL_URL, key);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean checkIsYouTube() {
        return Api.YOUTUBE.equalsIgnoreCase(site);
    }
}
