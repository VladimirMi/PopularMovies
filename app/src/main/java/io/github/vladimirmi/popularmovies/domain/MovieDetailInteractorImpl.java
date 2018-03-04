package io.github.vladimirmi.popularmovies.domain;

import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.data.net.NetworkChecker;
import io.github.vladimirmi.popularmovies.presentation.moviedetails.MovieDetailInteractor;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public class MovieDetailInteractorImpl implements MovieDetailInteractor {

    private final DataRepository mRepository;
    private final NetworkChecker mChecker;

    @Inject
    public MovieDetailInteractorImpl(DataRepository repository, NetworkChecker checker) {
        mRepository = repository;
        mChecker = checker;
    }

    @Override
    public Single<List<Video>> getTrailers(int movieId) {
        if (mChecker.isAvailableNet()) {
            return mRepository.getTrailersFromNet(String.valueOf(movieId));
        } else {
            return mRepository.getTrailersFromDb(String.valueOf(movieId));
        }
    }

    @Override
    public Single<List<Review>> getReviews(int movieId, int page) {
        if (mChecker.isAvailableNet()) {
            return mRepository.getReviewsFromNet(String.valueOf(movieId), page);
        } else {
            return mRepository.getReviewsFromDb(String.valueOf(movieId));
        }
    }

    @Override
    public Single<Boolean> isFavorite(int movieId) {
        return mRepository.getFavoriteMovie(String.valueOf(movieId))
                .map(movie -> true)
                .onErrorReturn(throwable -> false);
    }

    @Override
    public Completable removeFavorite(int movieId) {
        return mRepository.removeFavorite(String.valueOf(movieId));
    }

    @Override
    public Completable addFavorite(Movie movie, List<Review> reviews, List<Video> videos) {
        return mRepository.addFavorite(movie, reviews, videos);
    }
}
