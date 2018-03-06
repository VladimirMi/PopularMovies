package io.github.vladimirmi.popularmovies.presentation.moviedetails;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public interface MovieDetailInteractor {

    Single<List<Video>> getTrailers(int movieId);

    Single<List<Review>> getReviews(int movieId, int page);

    Single<Boolean> isFavorite(int movieId);

    Completable removeFavorite(int movieId);

    Completable addFavorite(Movie movie, List<Review> reviews, List<Video> videos);

    boolean isNetAvailable();
}
