package io.github.vladimirmi.popularmovies.data.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.vladimirmi.popularmovies.data.db.MovieContract.MovieEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.ReviewEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.VideoEntry;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.reactivex.functions.Function;

/**
 * Created by Vladimir Mikhalev 05.03.2018.
 */

public class ValuesMapper {

    private ValuesMapper() {
    }

    public static Movie cursorToMovie(Cursor cursor) {
        return new Movie(
                cursor.getInt(cursor.getColumnIndex(MovieEntry._ID)),
                cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_RATING)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)),
                cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE))
        );
    }

    public static Review cursorToReview(Cursor cursor) {
        return new Review(
                cursor.getString(cursor.getColumnIndex(ReviewEntry._ID)),
                cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR)),
                cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT))
        );
    }

    public static Video cursorToVideo(Cursor cursor) {
        return new Video(
                cursor.getString(cursor.getColumnIndex(VideoEntry._ID)),
                cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_KEY)),
                cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_NAME))
        );
    }

    public static ContentValues createValue(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movie.getId());
        values.put(MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_POSTER, movie.getPosterPath());
        values.put(MovieEntry.COLUMN_BACKDROP, movie.getBackdropPath());
        values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
        return values;
    }

    public static ContentValues createValue(Review review, String movieId) {
        ContentValues values = new ContentValues();
        values.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
        values.put(ReviewEntry.COLUMN_CONTENT, review.getContent());
        values.put(ReviewEntry.COLUMN_SERVER_ID, review.getId());
        values.put(ReviewEntry.COLUMN_MOVIE_ID, movieId);
        return values;
    }

    public static ContentValues createValue(Video video, String movieId) {
        ContentValues values = new ContentValues();
        values.put(VideoEntry.COLUMN_KEY, video.getKey());
        values.put(VideoEntry.COLUMN_NAME, video.getName());
        values.put(VideoEntry.COLUMN_SERVER_ID, video.getId());
        values.put(VideoEntry.COLUMN_MOVIE_ID, movieId);
        return values;
    }

    public static <T> List<T> getList(Cursor cursor, Function<Cursor, T> mapper) {
        if (cursor.getCount() == 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            try {
                list.add(mapper.apply(cursor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    public static <T> ContentValues[] createValues(List<T> list, Function<T, ContentValues> mapper) {
        ContentValues[] values = new ContentValues[list.size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = mapper.apply(list.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return values;
    }
}
