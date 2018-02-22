package io.github.vladimirmi.popularmovies.data.net;

import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Rest service interface
 */

public interface RestService {

    @GET("movie/popular")
    Single<PaginatedMoviesResult> getPopular(@Query("page") int page);

    @GET("movie/top_rated")
    Single<PaginatedMoviesResult> getTopRated(@Query("page") int page);
}
