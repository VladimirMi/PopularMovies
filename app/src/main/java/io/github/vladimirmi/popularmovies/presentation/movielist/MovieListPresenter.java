package io.github.vladimirmi.popularmovies.presentation.movielist;

import android.os.Parcelable;
import android.util.SparseArray;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.R;
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

@SuppressWarnings("WeakerAccess")
public class MovieListPresenter extends BasePresenter<MovieListView> {

    private final MovieListInteractor mInteractor;
    private Sort mSortBy;
    private final List<Movie> mPopularMovies = new ArrayList<>();
    private final List<Movie> mTopMovies = new ArrayList<>();
    private Movie mLastSelected;
    private SparseArray<Parcelable> scrollBySort = new SparseArray<>();

    @Inject
    public MovieListPresenter(MovieListInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    protected void onFirstAttach(MovieListView view) {
        mSortBy = mInteractor.getSortBy();
        if (!mInteractor.isNetAvailable()) {
            view.showSnack(R.string.no_connection);
        }
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

    public void saveScrollState(Parcelable state) {
        scrollBySort.put(mSortBy.ordinal(), state);
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
        mView.restoreScrollState(scrollBySort.get(mSortBy.ordinal()));
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
                        mView.showLoading(false);
                        mView.setMovies(mPopularMovies);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (e instanceof SocketTimeoutException) {
                            mView.showSnack(R.string.no_connection);
                        }
                    }
                });
    }

    private Disposable fetchTopRatedMovies(int page) {
        return mInteractor.getTopRatedMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading(true))
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        initLastSelected(movies);
                        mTopMovies.addAll(movies);
                        mView.showLoading(false);
                        mView.setMovies(mTopMovies);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (e instanceof SocketTimeoutException) {
                            mView.showSnack(R.string.no_connection);
                        }
                    }
                });
    }

    private Disposable fetchFavoriteMovies() {
        return mInteractor.getFavoriteMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading(true))
                .subscribe(movies -> {
                    initLastSelected(movies);
                    if (movies.isEmpty()) {
                        mView.showSnack(R.string.empty_favorites);
                    }
                    mView.setMovies(movies);
                }, Timber::e);
    }

    public void setLastSelectedMovie(Movie movie) {
        mLastSelected = movie;
    }

    private void initLastSelected(List<Movie> movies) {
        if (!movies.isEmpty()) {
            mLastSelected = movies.get(0);
        }
    }
}
