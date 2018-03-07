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
import timber.log.Timber;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

@SuppressWarnings("WeakerAccess")
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
        String id = String.valueOf(movieId);
        return isFavorite(movieId)
                .flatMap(favorite -> {
                    if (favorite) {
                        return mRepository.getTrailersFromNet(id)
                                .flatMapCompletable(videos -> mRepository.updateTrailers(videos, id)
                                        .filter(updated -> updated == 0)
                                        .flatMapCompletable(integer -> mRepository.insertTrailers(videos, id)
                                                .toCompletable()))
                                .andThen(mRepository.getTrailersFromDb(id))
                                .onErrorResumeNext(throwable -> {
                                    Timber.e(throwable);
                                    return mRepository.getTrailersFromDb(id);
                                });
                    } else {
                        return mRepository.getTrailersFromNet(id);
                    }
                });
    }

    @Override
    public Single<List<Review>> getReviews(int movieId, int page) {
        String id = String.valueOf(movieId);
        return isFavorite(movieId)
                .flatMap(favorite -> {
                    if (favorite) {
                        return mRepository.getReviewsFromNet(id, page)
                                .flatMapCompletable(reviews -> mRepository.updateReviews(reviews, id)
                                        .filter(updated -> updated == 0)
                                        .flatMapCompletable(integer -> mRepository.insertReviews(reviews, id)
                                                .toCompletable()))
                                .andThen(mRepository.getReviewsFromDb(id))
                                .onErrorResumeNext(throwable -> {
                                    Timber.e(throwable);
                                    return mRepository.getReviewsFromDb(id);
                                });
                    } else {
                        return mRepository.getReviewsFromNet(id, page);
                    }
                });
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

    @Override
    public boolean isNetAvailable() {
        return mChecker.isAvailableNet();
    }
}
