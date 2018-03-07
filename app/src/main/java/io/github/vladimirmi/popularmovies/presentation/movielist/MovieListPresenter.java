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

    private Sort mSort;
    private final List<Movie> mPopularMovies = new ArrayList<>();
    private final List<Movie> mTopMovies = new ArrayList<>();
    private SparseArray<Parcelable> mScrollStateBySort = new SparseArray<>();
    private SparseArray<Movie> mSelectedBySort = new SparseArray<>();

    @Inject
    public MovieListPresenter(MovieListInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    protected void onFirstAttach(MovieListView view) {
        mSort = mInteractor.getSortBy();
        if (!mInteractor.isNetAvailable()) {
            view.showSnack(R.string.no_connection);
        }
        fetchMovies(1);
    }

    @Override
    protected void onAttach(MovieListView view) {
        view.setSortByPosition(mSort.ordinal());
        if (isFirstAttach) return;
        setMovies();
    }

    public void fetchMovies(int page) {
        mCompDisp.clear();
        switch (mSort) {
            case POPULAR:
                mCompDisp.add(fetchPopularMovies(page));
                break;
            case TOP_RATED:
                mCompDisp.add(fetchTopRatedMovies(page));
                break;
            case FAVORITE:
                mCompDisp.add(fetchFavoriteMovies());
                break;
        }
    }

    public void saveSortByPosition(int position) {
        Sort sortBy = Sort.values()[position];
        if (sortBy == mSort) return;
        mSort = sortBy;
        mInteractor.saveSortBy(mSort);
        mView.resetMoviesList();
        setMovies();
    }

    public void saveScrollState(Parcelable state) {
        mScrollStateBySort.put(mSort.ordinal(), state);
    }

    public void selectMovie(Movie movie) {
        mSelectedBySort.put(mSort.ordinal(), movie);
        boolean isSame = lastSelected() == null || lastSelected().getId() != movie.getId();
        mView.showDetails(movie, isSame);
    }

    private void setMovies() {
        if (mSort == Sort.POPULAR && !mPopularMovies.isEmpty()) {
            mView.setMovies(mPopularMovies);
            restoreState();
        } else if (mSort == Sort.TOP_RATED && !mTopMovies.isEmpty()) {
            mView.setMovies(mTopMovies);
            restoreState();
        } else {
            fetchMovies(1);
        }
    }

    private void restoreState() {
        mView.restoreScrollState(mScrollStateBySort.get(mSort.ordinal()));
        mView.restoreLastSelected(lastSelected());
    }

    private Disposable fetchPopularMovies(int page) {
        return mInteractor.getPopularMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading(true))
                .doOnDispose(() -> mView.showLoading(false))
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        mPopularMovies.addAll(movies);
                        mView.setMovies(mPopularMovies);
                        initLastSelected(movies);
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
                .doOnDispose(() -> mView.showLoading(false))
                .subscribeWith(new SimpleSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        mTopMovies.addAll(movies);
                        mView.setMovies(mTopMovies);
                        initLastSelected(movies);
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
                .subscribe(movies -> {
                    mView.setMovies(movies);

                    if (movies.isEmpty()) {
                        mView.showSnack(R.string.empty_favorites);
                        mView.hideDetails();
                    } else {
                        if (!movies.contains(lastSelected())) {
                            mSelectedBySort.put(mSort.ordinal(), movies.get(0));
                        }
                        restoreState();
                    }
                }, Timber::e);
    }

    private void initLastSelected(List<Movie> movies) {
        if (lastSelected() == null && !movies.isEmpty()) {
            mSelectedBySort.put(mSort.ordinal(), movies.get(0));
            mView.restoreLastSelected(lastSelected());
        }
    }

    private Movie lastSelected() {
        return mSelectedBySort.get(mSort.ordinal());
    }
}
