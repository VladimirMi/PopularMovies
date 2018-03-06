package io.github.vladimirmi.popularmovies.presentation.movielist;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.presentation.core.BaseView;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public interface MovieListView extends BaseView {

    void setMovies(List<Movie> movies);

    void setSortByPosition(int position);

    void resetMoviesList();

    void setSelected(Movie lastSelected);

    void showLoading(boolean show);

    void restoreScrollState(@Nullable Parcelable state);

    void showSnack(int stringResId);
}
