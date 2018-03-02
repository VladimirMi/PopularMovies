package io.github.vladimirmi.popularmovies.moviedetails;

import java.util.List;

import io.github.vladimirmi.popularmovies.core.BaseView;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public interface MovieDetailsView extends BaseView {

    void setMovie(Movie movie);

    void setTrailers(List<Video> videos);

    void setReviews(List<Review> reviews);
}
