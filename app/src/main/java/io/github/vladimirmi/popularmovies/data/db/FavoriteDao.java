package io.github.vladimirmi.popularmovies.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.db.MovieContract.MovieEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.ReviewEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.VideoEntry;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public class FavoriteDao {

    private final SQLiteDatabase mDb;

    @Inject
    public FavoriteDao(SQLiteDatabase db) {
        mDb = db;
    }

    public void addMovie(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movie.getId());
        values.put(MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_POSTER, movie.getPosterPath());
        values.put(MovieEntry.COLUMN_BACKDROP, movie.getBackdropPath());
        values.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
        mDb.insert(MovieEntry.TABLE_NAME, null, values);
    }

    public List<Movie> getMovies() {
        Cursor cursor = mDb.query(
                MovieEntry.TABLE_NAME,   // The table to query
                null,   // The array of columns to return (pass null to get all)
                null,   // The columns for the WHERE clause
                null,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                MovieEntry.COLUMN_TIMESTAMP + " DESC"   // The sort order
        );

        List<Movie> movies = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            movies.add(createMovieFromCursor(cursor));
        }
        cursor.close();
        return movies;
    }

    public Movie getMovie(String movieId) {
        String[] selectionArgs = {movieId};
        Cursor cursor = mDb.query(
                MovieEntry.TABLE_NAME,   // The table to query
                null,   // The array of columns to return (pass null to get all)
                MovieEntry._ID + " = ?",   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                null    // The sort order
        );

        cursor.moveToNext();
        Movie movie = createMovieFromCursor(cursor);
        cursor.close();
        return movie;
    }

    public void addVideos(List<Video> videos, String movieId) {
        for (Video video : videos) {
            mDb.insert(VideoEntry.TABLE_NAME, null, createValueFromVideo(video, movieId));
        }
    }

    public List<Video> getVideosForMovie(String movieId) {
        String[] selectionArgs = {movieId};
        Cursor cursor = mDb.query(
                VideoEntry.TABLE_NAME,   // The table to query
                null,   // The array of columns to return (pass null to get all)
                VideoEntry.COLUMN_MOVIE_ID + " = ?",   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                null    // The sort order
        );

        List<Video> videos = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            videos.add(createVideoFromCursor(cursor));
        }
        cursor.close();
        return videos;
    }

    public void addReviews(List<Review> reviews, String movieId) {
        for (Review review : reviews) {
            mDb.insert(ReviewEntry.TABLE_NAME, null, createValueFromReview(review, movieId));
        }
    }

    public List<Review> getReviewsForMovie(String movieId) {
        String[] selectionArgs = {movieId};
        Cursor cursor = mDb.query(
                ReviewEntry.TABLE_NAME,   // The table to query
                null,   // The array of columns to return (pass null to get all)
                ReviewEntry.COLUMN_MOVIE_ID + " = ?",   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                null    // The sort order
        );

        List<Review> reviews = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            reviews.add(createReviewFromCursor(cursor));
        }
        cursor.close();
        return reviews;
    }

    public void deleteMovie(String movieId) {
        String selectionMovie = VideoEntry._ID + " = ?";
        String selectionRelated = VideoEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {movieId};

        mDb.delete(MovieEntry.TABLE_NAME, selectionMovie, selectionArgs);
        mDb.delete(VideoEntry.TABLE_NAME, selectionRelated, selectionArgs);
        mDb.delete(ReviewEntry.TABLE_NAME, selectionRelated, selectionArgs);
    }

    private Movie createMovieFromCursor(Cursor cursor) {
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

    private Video createVideoFromCursor(Cursor cursor) {
        return new Video(
                cursor.getString(cursor.getColumnIndex(VideoEntry._ID)),
                cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_KEY)),
                cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_NAME))
        );
    }

    private ContentValues createValueFromVideo(Video video, String movieId) {
        ContentValues values = new ContentValues();

        values.put(VideoEntry.COLUMN_KEY, video.getKey());
        values.put(VideoEntry.COLUMN_NAME, video.getName());
        values.put(VideoEntry.COLUMN_MOVIE_ID, movieId);

        return values;
    }

    private Review createReviewFromCursor(Cursor cursor) {
        return new Review(
                cursor.getString(cursor.getColumnIndex(ReviewEntry._ID)),
                cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR)),
                cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT))
        );
    }

    private ContentValues createValueFromReview(Review review, String movieId) {
        ContentValues values = new ContentValues();

        values.put(ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
        values.put(ReviewEntry.COLUMN_CONTENT, review.getContent());
        values.put(ReviewEntry.COLUMN_MOVIE_ID, movieId);

        return values;
    }

}
