package io.github.vladimirmi.popularmovies.presentation.moviedetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.presentation.core.BasePresenter;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class MovieDetailsPresenter extends BasePresenter<MovieDetailsView> {

    private final MovieDetailInteractor mInteractor;
    private Movie mMovie;
    private boolean mIsFavorite;
    private List<Video> mVideos = new ArrayList<>();
    private List<Review> mReviews = new ArrayList<>();

    @Inject
    public MovieDetailsPresenter(MovieDetailInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    protected void onFirstAttach(MovieDetailsView view) {
        mCompDisp.add(fetchTrailers());
        mCompDisp.add(fetchReviews(1));
        mCompDisp.add(isFavorite());
    }

    @Override
    protected void onAttach(MovieDetailsView view) {
        view.setMovie(mMovie);

        if (isFirstAttach) return;

        view.setIsFavorite(mIsFavorite);
        view.setTrailers(mVideos);
        view.setReviews(mReviews);
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }

    public void loadMoreReviews(int page) {
        mCompDisp.add(fetchReviews(page));
    }

    private Disposable fetchTrailers() {
        return mInteractor.getTrailers(mMovie.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Video>>() {
                    @Override
                    public void onSuccess(List<Video> videos) {
                        mVideos = videos;
                        mView.setTrailers(videos);
                    }
                });
    }

    private Disposable fetchReviews(int page) {
        return mInteractor.getReviews(mMovie.getId(), page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Review>>() {
                    @Override
                    public void onSuccess(List<Review> reviews) {
                        mReviews.addAll(reviews);
                        mView.setReviews(mReviews);
                    }
                });
    }

    private Disposable isFavorite() {
        return mInteractor.isFavorite(mMovie.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mIsFavorite = aBoolean;
                        mView.setIsFavorite(mIsFavorite);
                    }
                });
    }

    public void switchFavorite() {
        if (mIsFavorite) {
            mCompDisp.add(mInteractor.removeFavorite(mMovie.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mView.showToast(R.string.toast_favorite_remove),
                            Timber::e));
        } else {
            mCompDisp.add(mInteractor.addFavorite(mMovie, mReviews, mVideos)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> mView.showToast(R.string.toast_favorite_add),
                            Timber::e));
        }
        mIsFavorite = !mIsFavorite;
        mView.setIsFavorite(mIsFavorite);
    }
}
