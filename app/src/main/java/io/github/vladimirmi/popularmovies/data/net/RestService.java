package io.github.vladimirmi.popularmovies.data.net;

import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedReviewsResult;
import io.github.vladimirmi.popularmovies.data.entity.VideosResult;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Rest service interface
 */

public interface RestService {

    @GET("movie/popular")
    Single<PaginatedMoviesResult> getPopular(@Query("page") int page);

    @GET("movie/top_rated")
    Single<PaginatedMoviesResult> getTopRated(@Query("page") int page);

    @GET("movie/{movieId}/videos")
    Single<VideosResult> getTrailers(@Path("movieId") String movieId);

    @GET("movie/{movieId}/reviews")
    Single<PaginatedReviewsResult> getReviews(@Path("movieId") String movieId,
                                              @Query("page") int page);
}
