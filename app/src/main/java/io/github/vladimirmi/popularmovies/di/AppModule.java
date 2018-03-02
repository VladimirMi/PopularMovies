package io.github.vladimirmi.popularmovies.di;

import android.content.Context;

import io.github.vladimirmi.popularmovies.data.DataManager;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.github.vladimirmi.popularmovies.data.net.RestServiceProvider;
import io.github.vladimirmi.popularmovies.data.preferences.PreferencesManager;
import io.github.vladimirmi.popularmovies.movielist.MovieListPresenter;
import toothpick.config.Module;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class AppModule extends Module {

    public AppModule(Context context) {
        RestService service = RestServiceProvider.getService();
        PreferencesManager preferencesManager = new PreferencesManager(context);

        bind(DataManager.class).toInstance(new DataManager(service, preferencesManager));
        bind(MovieListPresenter.class).singletonInScope();
    }
}
