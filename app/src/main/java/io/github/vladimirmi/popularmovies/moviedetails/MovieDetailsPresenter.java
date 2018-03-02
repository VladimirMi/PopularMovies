package io.github.vladimirmi.popularmovies.moviedetails;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vladimirmi.popularmovies.core.BasePresenter;
import io.github.vladimirmi.popularmovies.data.DataManager;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class MovieDetailsPresenter extends BasePresenter<MovieDetailsView> {

    private final DataManager mDataManager;
    private Movie mMovie;
    private List<Video> mVideos;
    private List<Review> mReviews;

    @Inject
    public MovieDetailsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onFirstAttach(MovieDetailsView view) {
        mCompDisp.add(fetchTrailers());
        mCompDisp.add(fetchReviews(1));
    }

    @Override
    protected void onAttach(MovieDetailsView view) {
        view.setMovie(mMovie);
        if (isFirstAttach) return;

        view.setTrailers(mVideos);
        view.setReviews(mReviews);
    }

    void setMovie(Movie movie) {
        mMovie = movie;
    }

    void loadMoreReviews(int page) {
        mCompDisp.add(fetchReviews(page));
    }

    private Disposable fetchTrailers() {
        return mDataManager.getTrailers(String.valueOf(mMovie.getId()))
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
        return mDataManager.getReviwes(String.valueOf(mMovie.getId()), page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Review>>() {
                    @Override
                    public void onSuccess(List<Review> reviews) {
                        if (mReviews == null) {
                            mReviews = new ArrayList<>(reviews);
                        } else {
                            mReviews.addAll(reviews);
                        }
                        mView.setReviews(mReviews);
                    }
                });
    }
}
