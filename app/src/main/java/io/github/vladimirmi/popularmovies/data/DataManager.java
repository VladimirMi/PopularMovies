package io.github.vladimirmi.popularmovies.data;

import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 22.02.2018.
 */

public class DataManager {

    private final RestService mRestService;

    public DataManager(RestService restService) {
        mRestService = restService;
    }

    public Single<PaginatedMoviesResult> getPopularMovies(int page) {
        return mRestService.getPopular(page);
    }

    public Single<PaginatedMoviesResult> getTopRatedMovies(int page) {
        return mRestService.getTopRated(page);
    }
}
