package io.github.vladimirmi.popularmovies.movielist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.core.BasePresenter;
import io.github.vladimirmi.popularmovies.data.DataManager;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class MovieListPresenter extends BasePresenter<MovieListView> {

    private DataManager mDataManager;
    private Sort mSortBy;
    private List<Movie> mPopularMovies;
    private List<Movie> mTopMovies;
    private Disposable loadPopular;
    private Disposable loadTopRated;

    @Inject
    public MovieListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onFirstAttach(MovieListView view) {
        mSortBy = mDataManager.getSortBy();
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
                if (loadTopRated != null) loadTopRated.dispose();
                loadPopular = fetchPopularMovies(page);
                break;
            case TOP_RATED:
                if (loadPopular != null) loadPopular.dispose();
                loadTopRated = fetchTopRatedMovies(page);
                break;
        }
    }

    public void saveSortByPosition(int position) {
        Sort sortBy = Sort.values()[position];
        if (sortBy == mSortBy) return;
        mSortBy = sortBy;
        mDataManager.saveSortBy(mSortBy);
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
        }
    }

    private void setOrFetchIfNull(List<Movie> movies) {
        if (movies != null) {
            mView.setMovies(movies);
        } else {
            fetchMovies(1);
        }
    }

    private Disposable fetchPopularMovies(int page) {
        return mDataManager.getPopularMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        if (mPopularMovies == null) {
                            mPopularMovies = new ArrayList<>(movies);
                        } else {
                            mPopularMovies.addAll(movies);
                        }
                        mView.setMovies(mPopularMovies);
                    }
                });
    }

    private Disposable fetchTopRatedMovies(int page) {
        return mDataManager.getTopRatedMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        if (mTopMovies == null) {
                            mTopMovies = new ArrayList<>(movies);
                        } else {
                            mTopMovies.addAll(movies);
                        }
                        mView.setMovies(mTopMovies);
                    }
                });
    }

    public enum Sort {
        POPULAR, TOP_RATED
    }
}
