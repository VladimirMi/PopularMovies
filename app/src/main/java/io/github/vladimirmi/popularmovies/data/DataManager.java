package io.github.vladimirmi.popularmovies.data;

import io.github.vladimirmi.popularmovies.data.net.RestService;

/**
 * Created by Vladimir Mikhalev 22.02.2018.
 */

public class DataManager {

    private final RestService mRestService;

    public DataManager(RestService restService) {
        mRestService = restService;
    }
}
