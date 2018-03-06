package io.github.vladimirmi.popularmovies.domain;

import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Sort;
import io.github.vladimirmi.popularmovies.data.net.NetworkChecker;
import io.github.vladimirmi.popularmovies.presentation.movielist.MovieListInteractor;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public class MovieListIntearactorImpl implements MovieListInteractor {

    private final DataRepository mRepository;
    private final NetworkChecker mChecker;

    @Inject
    public MovieListIntearactorImpl(DataRepository repository, NetworkChecker checker) {
        mRepository = repository;
        mChecker = checker;
    }

    @Override
    public Single<List<Movie>> getPopularMovies(int page) {
        return mRepository.getPopularMovies(page);
    }

    @Override
    public Single<List<Movie>> getTopRatedMovies(int page) {
        return mRepository.getTopRatedMovies(page);
    }

    @Override
    public Observable<List<Movie>> getFavoriteMovies() {
        return mRepository.getFavoriteMovies();
    }

    @Override
    public void saveSortBy(Sort sortBy) {
        mRepository.saveSortBy(sortBy);
    }

    @Override
    public Sort getSortBy() {
        Sort sortBy;
        if (mChecker.isAvailableNet()) {
            sortBy = mRepository.getSortBy();
        } else {
            sortBy = Sort.FAVORITE;
            mRepository.saveSortBy(sortBy);
        }
        return sortBy;
    }

    @Override
    public boolean isNetAvailable() {
        return mChecker.isAvailableNet();
    }
}
