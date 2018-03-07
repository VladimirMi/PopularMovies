package io.github.vladimirmi.popularmovies.data.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.vladimirmi.popularmovies.di.AppModule;
import io.github.vladimirmi.popularmovies.di.Scopes;

/**
 * Created by Vladimir Mikhalev 05.03.2018.
 */

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIE_FOR_ID = 101;
    private static final int REVIEWS = 200;
    private static final int REVIEW_FOR_ID = 201;
    private static final int REVIEWS_FOR_MOVIE = 202;
    private static final int VIDEOS = 300;
    private static final int VIDEO_FOR_ID = 301;
    private static final int VIDEOS_FOR_MOVIE = 302;

    private UriMatcher mMatcher;
    private SQLiteDatabase mDb;
    private ContentResolver mResolver;

    @Override
    public boolean onCreate() {
        Scopes.getAppScope().installModules(new AppModule(getContext()));
        mMatcher = buildUriMatcher();
        mDb = Scopes.getAppScope().getInstance(SQLiteDatabase.class);
        mResolver = Scopes.getAppScope().getInstance(ContentResolver.class);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = mMatcher.match(uri);

        String tableName;

        switch (match) {
            case MOVIES:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                sortOrder = MovieContract.MovieEntry.COLUMN_TIMESTAMP + " DESC";
                break;
            case REVIEWS_FOR_MOVIE:
                tableName = MovieContract.ReviewEntry.TABLE_NAME;
                selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case MOVIE_FOR_ID:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                selection = MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case VIDEOS_FOR_MOVIE:
                tableName = MovieContract.VideoEntry.TABLE_NAME;
                selection = MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Cursor cursor = mDb.query(
                tableName,   // The table to query
                projection,   // The array of columns to return (pass null to get all)
                selection,   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                sortOrder   // The sort order
        );
        cursor.setNotificationUri(mResolver, uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = mMatcher.match(uri);

        Uri returnUri = null;

        switch (match) {
            case MOVIES:
                long id = mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                }
                break;
            case REVIEWS:
                id = mDb.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.ReviewEntry.CONTENT_URI, id);
                }
                break;
            case VIDEOS:
                id = mDb.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.VideoEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (returnUri == null) throw new SQLException("Failed to insert row into " + uri);

        mResolver.notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mMatcher.match(uri);
        String tableName;

        switch (match) {
            case MOVIE_FOR_ID:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                selection = MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case REVIEWS_FOR_MOVIE:
                tableName = MovieContract.ReviewEntry.TABLE_NAME;
                selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case VIDEOS_FOR_MOVIE:
                tableName = MovieContract.VideoEntry.TABLE_NAME;
                selection = MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int deleted = mDb.delete(tableName, selection, selectionArgs);

        if (deleted != 0) {
            mResolver.notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mMatcher.match(uri);
        String tableName;

        switch (match) {
            case MOVIE_FOR_ID:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                selection = MovieContract.MovieEntry._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case REVIEW_FOR_ID:
                tableName = MovieContract.ReviewEntry.TABLE_NAME;
                selection = MovieContract.ReviewEntry.COLUMN_SERVER_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            case VIDEO_FOR_ID:
                tableName = MovieContract.VideoEntry.TABLE_NAME;
                selection = MovieContract.VideoEntry.COLUMN_SERVER_ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//        Timber.e("update: %s", uri);
        int updated = mDb.update(tableName, values, selection, selectionArgs);

        if (updated != 0) {
            mResolver.notifyChange(uri, null);
        }
        return updated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_FOR_ID);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEWS_FOR_MOVIE);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS + "/#", VIDEOS_FOR_MOVIE);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/*", REVIEW_FOR_ID);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS + "/*", VIDEO_FOR_ID);

        return matcher;
    }
}
