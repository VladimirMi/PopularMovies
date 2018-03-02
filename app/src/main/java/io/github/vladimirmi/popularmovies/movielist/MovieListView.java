package io.github.vladimirmi.popularmovies.movielist;

import java.util.List;

import io.github.vladimirmi.popularmovies.core.BaseView;
import io.github.vladimirmi.popularmovies.data.entity.Movie;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public interface MovieListView extends BaseView {

    void setMovies(List<Movie> movies);

    void setSortByPosition(int position);

    void resetMoviesList();
}
