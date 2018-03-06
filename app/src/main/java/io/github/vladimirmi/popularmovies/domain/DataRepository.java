package io.github.vladimirmi.popularmovies.domain;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Sort;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public interface DataRepository {

    Single<List<Movie>> getPopularMovies(int page);

    Single<List<Movie>> getTopRatedMovies(int page);

    Observable<List<Movie>> getFavoriteMovies();

    Single<Movie> getFavoriteMovie(String movieId);

    Single<List<Video>> getTrailersFromNet(String movieId);

    Single<List<Review>> getReviewsFromNet(String movieId, int page);

    Single<List<Video>> getTrailersFromDb(String movieId);

    Single<List<Review>> getReviewsFromDb(String movieId);

    void saveSortBy(Sort sortBy);

    Sort getSortBy();

    Completable removeFavorite(String movieId);

    Completable addFavorite(Movie movie, List<Review> reviews, List<Video> videos);

    Single<Boolean> insertMovie(Movie movie);

    Single<Boolean> insertReviews(List<Review> reviews, String movieId);

    Single<Boolean> insertTrailers(List<Video> videos, String movieId);

    Single<Integer> updateMovie(Movie movie);

    Single<Integer> updateReviews(List<Review> reviews, String movieId);

    Single<Integer> updateTrailers(List<Video> videos, String movieId);
}
