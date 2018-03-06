package io.github.vladimirmi.popularmovies.presentation.moviedetails;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.presentation.core.BaseView;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public interface MovieDetailsView extends BaseView {

    void setMovie(Movie movie);

    void setIsFavorite(boolean isFavorite);

    void setTrailers(List<Video> videos);

    void setReviews(List<Review> reviews);

    void showSnack(int stringResId);
}
