package io.github.vladimirmi.popularmovies.data.db;


import android.net.Uri;
import android.provider.BaseColumns;

import io.github.vladimirmi.popularmovies.BuildConfig;

@SuppressWarnings("WeakerAccess")
public final class MovieContract {

    private MovieContract() {
    }

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER = "posterPath";
        public static final String COLUMN_BACKDROP = "backdropPath";
        public static final String COLUMN_TIMESTAMP = "createdAt";
    }

    public static class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_SERVER_ID = "serverId";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }

    public static class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEOS).build();

        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_SERVER_ID = "serverId";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KEY = "urlKey";
    }
}
