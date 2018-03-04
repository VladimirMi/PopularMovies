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

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class MovieListPresenter extends BasePresenter<MovieListView> {

    private MovieListInteractor mInteractor;
    private Sort mSortBy;
    private List<Movie> mPopularMovies;
    private List<Movie> mTopMovies;
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
                setOrFetchIfNull(mPopularMovies);
                break;
            case TOP_RATED:
                setOrFetchIfNull(mTopMovies);
                break;
            case FAVORITE:
                fetchMovies(1);
                break;
        }
        mView.setSelected(mLastSelected);
    }

    private void setOrFetchIfNull(List<Movie> movies) {
        if (movies != null) {
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
                        if (mPopularMovies == null) {
                            mPopularMovies = new ArrayList<>(movies);
                            mLastSelected = movies.get(0);
                            mView.setSelected(mLastSelected);
                        } else {
                            mPopularMovies.addAll(movies);
                        }
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
                        if (mTopMovies == null) {
                            mTopMovies = new ArrayList<>(movies);
                            mLastSelected = movies.get(0);
                            mView.setSelected(mLastSelected);
                        } else {
                            mTopMovies.addAll(movies);
                        }
                        mView.setMovies(mTopMovies);
                    }
                });
    }

    private Disposable fetchFavoriteMovies() {
        return mInteractor.getFavoriteMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        if (mLastSelected == null && !movies.isEmpty()) {
                            mLastSelected = movies.get(0);
                            mView.setSelected(mLastSelected);
                        }
                        mView.setMovies(movies);
                    }
                });
    }

    public void setLastSelectedMovie(Movie movie) {
        mLastSelected = movie;
    }
}
