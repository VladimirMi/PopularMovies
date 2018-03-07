package io.github.vladimirmi.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.db.MovieContract;
import io.github.vladimirmi.popularmovies.data.db.ValuesMapper;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedReviewsResult;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Sort;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.data.entity.VideosResult;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.github.vladimirmi.popularmovies.data.preferences.Preferences;
import io.github.vladimirmi.popularmovies.domain.DataRepository;
import io.github.vladimirmi.popularmovies.utils.ContentObservable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Facade class for working with data layer.
 */

public class DataRepositoryImpl implements DataRepository {

    private final RestService mRestService;
    private final Preferences mPreferences;
    private final ContentResolver mResolver;

    @Inject
    public DataRepositoryImpl(RestService restService, Preferences preferences, ContentResolver resolver) {
        mRestService = restService;
        mPreferences = preferences;
        mResolver = resolver;
    }

    @Override
    public Single<List<Movie>> getPopularMovies(int page) {
        return mRestService.getPopular(page)
                .map(PaginatedMoviesResult::getResults);
    }

    @Override
    public Single<List<Movie>> getTopRatedMovies(int page) {
        return mRestService.getTopRated(page)
                .map(PaginatedMoviesResult::getResults);
    }

    @Override
    public Observable<List<Movie>> getFavoriteMovies() {
        return ContentObservable.create(mResolver, MovieContract.MovieEntry.CONTENT_URI, true)
                .map(cursor -> ValuesMapper.getList(cursor, ValuesMapper::cursorToMovie));
    }

    @Override
    public Single<Movie> getFavoriteMovie(String movieId) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        return ContentObservable.create(mResolver, uri, false)
                .map(cursor -> {
                    cursor.moveToNext();
                    return ValuesMapper.cursorToMovie(cursor);
                })
                .firstOrError();
    }

    @Override
    public Single<List<Video>> getTrailersFromNet(String movieId) {
        return mRestService.getTrailers(movieId)
                .map(VideosResult::getResults);
    }

    @Override
    public Single<List<Review>> getReviewsFromNet(String movieId, int page) {
        return mRestService.getReviews(movieId, page)
                .map(PaginatedReviewsResult::getResults);
    }

    @Override
    public Single<List<Video>> getTrailersFromDb(String movieId) {
        Uri uri = MovieContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        return ContentObservable.create(mResolver, uri, false)
                .map(cursor -> ValuesMapper.getList(cursor, ValuesMapper::cursorToVideo))
                .firstOrError();
    }

    @Override
    public Single<List<Review>> getReviewsFromDb(String movieId) {
        Uri uri = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        return ContentObservable.create(mResolver, uri, false)
                .map(cursor -> ValuesMapper.getList(cursor, ValuesMapper::cursorToReview))
                .firstOrError();
    }

    @Override
    public void saveSortBy(Sort sortBy) {
        mPreferences.sortByOrderPref.put(sortBy.name());
    }

    @Override
    public Sort getSortBy() {
        return Sort.valueOf(mPreferences.sortByOrderPref.get());
    }

    @Override
    public Completable removeFavorite(String movieId) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        Uri reviewUri = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        Uri videoUri = MovieContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        return Completable.mergeArray(
                Completable.fromAction(() -> mResolver.delete(movieUri, null, null)),
                Completable.fromAction(() -> mResolver.delete(reviewUri, null, null)),
                Completable.fromAction(() -> mResolver.delete(videoUri, null, null))
        ).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable addFavorite(Movie movie, List<Review> reviews, List<Video> videos) {
        String movieId = String.valueOf(movie.getId());

        return Single.merge(insertMovie(movie), insertReviews(reviews, movieId),
                insertTrailers(videos, movieId))
                .ignoreElements()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertMovie(Movie movie) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        ContentValues movieValue = ValuesMapper.createValue(movie);

        return Single.fromCallable(() -> mResolver.insert(movieUri, movieValue))
                .map(uri -> uri != null)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertReviews(List<Review> reviews, String movieId) {
        Uri reviewUri = MovieContract.ReviewEntry.CONTENT_URI;

        ContentValues[] reviewsValue =
                ValuesMapper.createValues(reviews, review -> ValuesMapper.createValue(review, movieId));

        return Single.fromCallable(() -> mResolver.bulkInsert(reviewUri, reviewsValue))
                .map(integer -> integer > 0)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertTrailers(List<Video> videos, String movieId) {
        Uri videoUri = MovieContract.VideoEntry.CONTENT_URI;

        ContentValues[] videosValue =
                ValuesMapper.createValues(videos, video -> ValuesMapper.createValue(video, movieId));

        return Single.fromCallable(() -> mResolver.bulkInsert(videoUri, videosValue))
                .map(uri -> uri != null)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Integer> updateMovie(Movie movie) {
        String movieId = String.valueOf(movie.getId());
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        ContentValues movieValue = ValuesMapper.createValue(movie);

        return Single.fromCallable(() -> mResolver.update(movieUri, movieValue, null, null))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Integer> updateReviews(List<Review> reviews, String movieId) {
        Uri baseUri = MovieContract.ReviewEntry.CONTENT_URI;

        return Observable.fromIterable(reviews)
                .map(review -> mResolver.update(baseUri.buildUpon().appendPath(review.getId()).build(),
                        ValuesMapper.createValue(review, movieId),
                        null, null))
                .scan((count1, count2) -> count1 + count2)
                .last(0)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Integer> updateTrailers(List<Video> videos, String movieId) {
        Uri baseUri = MovieContract.VideoEntry.CONTENT_URI;

        return Observable.fromIterable(videos)
                .map(video -> mResolver.update(baseUri.buildUpon().appendPath(video.getId()).build(),
                        ValuesMapper.createValue(video, movieId),
                        null, null))
                .scan((count1, count2) -> count1 + count2)
                .last(0)
                .subscribeOn(Schedulers.io());
    }
}
