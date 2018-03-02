package io.github.vladimirmi.popularmovies.data;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedReviewsResult;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.data.entity.VideosResult;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.github.vladimirmi.popularmovies.data.preferences.PreferencesManager;
import io.github.vladimirmi.popularmovies.movielist.MovieListPresenter;
import io.reactivex.Single;

/**
 * Facade class for working with data layer.
 */

public class DataManager {

    private final RestService mRestService;
    private final PreferencesManager mPreferencesManager;

    public DataManager(RestService restService, PreferencesManager preferencesManager) {
        mRestService = restService;
        mPreferencesManager = preferencesManager;
    }

    //region =============== Network ==============

    public Single<List<Movie>> getPopularMovies(int page) {
        return mRestService.getPopular(page)
                .map(PaginatedMoviesResult::getResults);
    }

    public Single<List<Movie>> getTopRatedMovies(int page) {
        return mRestService.getTopRated(page)
                .map(PaginatedMoviesResult::getResults);
    }

    public Single<List<Video>> getTrailers(String movieId) {
        return mRestService.getTrailers(movieId)
                .map(VideosResult::getResults);
    }

    public Single<List<Review>> getReviwes(String movieId, int page) {
        return mRestService.getReviews(movieId, page)
                .map(PaginatedReviewsResult::getResults);
    }

    //endregion

    //region =============== Shared Preferences ==============

    public void saveSortBy(MovieListPresenter.Sort sortBy) {
        mPreferencesManager.sortByOrderPref.put(sortBy.name());
    }

    public MovieListPresenter.Sort getSortBy() {
        return MovieListPresenter.Sort.valueOf(mPreferencesManager.sortByOrderPref.get());
    }

    //endregion
}
