package io.github.vladimirmi.popularmovies.data;

import io.github.vladimirmi.popularmovies.MovieListActivity;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.github.vladimirmi.popularmovies.data.preferences.PreferencesManager;
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

    public Single<PaginatedMoviesResult> getPopularMovies(int page) {
        return mRestService.getPopular(page);
    }

    public Single<PaginatedMoviesResult> getTopRatedMovies(int page) {
        return mRestService.getTopRated(page);
    }

    //endregion

    //region =============== Shared Preferences ==============

    public void saveSortBy(MovieListActivity.Sort sortBy) {
        mPreferencesManager.sortByOrderPref.put(sortBy.name());
    }

    public MovieListActivity.Sort getSortBy() {
        return MovieListActivity.Sort.valueOf(mPreferencesManager.sortByOrderPref.get());
    }

    //endregion
}
