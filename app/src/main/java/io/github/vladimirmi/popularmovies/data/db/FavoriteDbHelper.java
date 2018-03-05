package io.github.vladimirmi.popularmovies.data.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.vladimirmi.popularmovies.data.db.MovieContract.MovieEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.ReviewEntry;
import io.github.vladimirmi.popularmovies.data.db.MovieContract.VideoEntry;

public class FavoriteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
            MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY," +
            MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
            MovieEntry.COLUMN_RATING + " REAL NOT NULL," +
            MovieEntry.COLUMN_RELEASE + " TEXT NOT NULL," +
            MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
            MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
            MovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL," +
            MovieEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " +
            VideoEntry.TABLE_NAME + " (" +
            VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            VideoEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL," +
            VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
            VideoEntry.COLUMN_KEY + " TEXT NOT NULL)";

    private static final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
            ReviewEntry.TABLE_NAME + " (" +
            ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
            ReviewEntry.COLUMN_SERVER_ID + " TEXT UNIQUE NOT NULL," +
            ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
            ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL)";


    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
