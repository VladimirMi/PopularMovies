package io.github.vladimirmi.popularmovies.data.net;

import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Rest service interface
 */

public interface RestService {

    @GET("movie/popular")
    PaginatedMoviesResult getPopular(@Query("page") int page);

    @GET("movie/top_rated")
    PaginatedMoviesResult getTopRated(@Query("page") int page);
}
