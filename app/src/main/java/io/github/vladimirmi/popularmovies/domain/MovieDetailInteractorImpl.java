package io.github.vladimirmi.popularmovies.domain;

import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.presentation.moviedetails.MovieDetailInteractor;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public class MovieDetailInteractorImpl implements MovieDetailInteractor {

    private final DataRepository mRepository;

    @Inject
    public MovieDetailInteractorImpl(DataRepository repository) {
        mRepository = repository;
    }

    @Override
    public Single<List<Video>> getTrailers(int movieId) {
        String id = String.valueOf(movieId);
        return isFavorite(movieId)
                .flatMap(favorite -> {
                    if (favorite) {
                        return mRepository.getTrailersFromNet(id)
                                .flatMapCompletable(videos -> mRepository.updateTrailers(videos, id))
                                .andThen(mRepository.getTrailersFromDb(id))
                                .onErrorResumeNext(mRepository.getTrailersFromDb(id));
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
                                .flatMapCompletable(videos -> mRepository.updateReviews(videos, id))
                                .andThen(mRepository.getReviewsFromDb(id))
                                .onErrorResumeNext(mRepository.getReviewsFromDb(id));
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
}
