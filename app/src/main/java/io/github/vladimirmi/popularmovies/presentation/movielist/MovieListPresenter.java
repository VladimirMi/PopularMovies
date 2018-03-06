package io.github.vladimirmi.popularmovies.presentation.movielist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Sort;
import io.github.vladimirmi.popularmovies.presentation.core.BasePresenter;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class MovieListPresenter extends BasePresenter<MovieListView> {

    private final MovieListInteractor mInteractor;
    private Sort mSortBy;
    private final List<Movie> mPopularMovies = new ArrayList<>();
    private final List<Movie> mTopMovies = new ArrayList<>();
    private Movie mLastSelected;

    @Inject
    public MovieListPresenter(MovieListInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    protected void onFirstAttach(MovieListView view) {
        mSortBy = mInteractor.getSortBy();
        fetchMovies(1);
    }

    @Override
    protected void onAttach(MovieListView view) {
        view.setSortByPosition(mSortBy.ordinal());
        if (isFirstAttach) return;
        setMovies();
    }

    public void fetchMovies(int page) {
        mView.showLoading(true);
        switch (mSortBy) {
            case POPULAR:
                mCompDisp.clear();
                mCompDisp.add(fetchPopularMovies(page));
                break;
            case TOP_RATED:
                mCompDisp.clear();
                mCompDisp.add(fetchTopRatedMovies(page));
                break;
            case FAVORITE:
                mCompDisp.clear();
                mCompDisp.add(fetchFavoriteMovies());
                break;
        }
    }

    public void saveSortByPosition(int position) {
        Sort sortBy = Sort.values()[position];
        if (sortBy == mSortBy) return;
        mSortBy = sortBy;
        mInteractor.saveSortBy(mSortBy);
        mView.resetMoviesList();
        setMovies();
    }

    private void setMovies() {
        switch (mSortBy) {
            case POPULAR:
                setOrFetchIfEmpty(mPopularMovies);
                break;
            case TOP_RATED:
                setOrFetchIfEmpty(mTopMovies);
                break;
            case FAVORITE:
                fetchMovies(1);
                break;
        }
        mView.setSelected(mLastSelected);
    }

    private void setOrFetchIfEmpty(List<Movie> movies) {
        if (!movies.isEmpty()) {
            mView.setMovies(movies);
        } else {
            fetchMovies(1);
        }
    }

    private Disposable fetchPopularMovies(int page) {
        return mInteractor.getPopularMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        initLastSelected(movies);
                        mPopularMovies.addAll(movies);
                        mView.setMovies(mPopularMovies);
                    }
                });
    }

    private Disposable fetchTopRatedMovies(int page) {
        return mInteractor.getTopRatedMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        initLastSelected(movies);
                        mTopMovies.addAll(movies);
                        mView.setMovies(mTopMovies);
                    }
                });
    }

    private Disposable fetchFavoriteMovies() {
        return mInteractor.getFavoriteMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    initLastSelected(movies);
                    mView.setMovies(movies);
                }, Timber::e);
    }

    public void setLastSelectedMovie(Movie movie) {
        mLastSelected = movie;
    }

    private void initLastSelected(List<Movie> movies) {
        if (mLastSelected == null && !movies.isEmpty()) {
            mLastSelected = movies.get(0);
            mView.setSelected(mLastSelected);
        }
    }
}
