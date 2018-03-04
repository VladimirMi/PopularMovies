package io.github.vladimirmi.popularmovies.data;

import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.db.FavoriteDao;
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
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Facade class for working with data layer.
 */

public class DataManager implements DataRepository {

    private final RestService mRestService;
    private final Preferences mPreferences;
    private final FavoriteDao mDao;

    @Inject
    public DataManager(RestService restService, Preferences preferences, FavoriteDao dao) {
        mRestService = restService;
        mPreferences = preferences;
        mDao = dao;
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
    public Single<List<Movie>> getFavoriteMovies() {
        return Single.fromCallable(mDao::getMovies)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Movie> getFavoriteMovie(String movieId) {
        return Single.fromCallable(() -> mDao.getMovie(movieId))
                .subscribeOn(Schedulers.io());
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
        return Single.fromCallable(() -> mDao.getVideosForMovie(movieId))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<List<Review>> getReviewsFromDb(String movieId) {
        return Single.fromCallable(() -> mDao.getReviewsForMovie(movieId))
                .subscribeOn(Schedulers.io());
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
        return Completable.fromRunnable(() -> mDao.deleteMovie(movieId))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable addFavorite(Movie movie, List<Review> reviews, List<Video> videos) {
        return Completable.fromRunnable(() -> mDao.addMovie(movie))
                .andThen(Completable.fromRunnable(() -> mDao.addReviews(reviews, String.valueOf(movie.getId()))))
                .andThen(Completable.fromRunnable(() -> mDao.addVideos(videos, String.valueOf(movie.getId()))))
                .subscribeOn(Schedulers.io());
    }
}
