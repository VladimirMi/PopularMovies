package io.github.vladimirmi.popularmovies.presentation.movielist;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Sort;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by Vladimir Mikhalev 03.03.2018.
 */

public interface MovieListInteractor {

    Single<List<Movie>> getPopularMovies(int page);

    Single<List<Movie>> getTopRatedMovies(int page);

    Observable<List<Movie>> getFavoriteMovies();

    void saveSortBy(Sort sortBy);

    Sort getSortBy();

    boolean isNetAvailable();
}
