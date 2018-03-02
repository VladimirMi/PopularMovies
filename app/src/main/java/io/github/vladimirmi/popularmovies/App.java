package io.github.vladimirmi.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import io.github.vladimirmi.popularmovies.di.AppModule;
import io.github.vladimirmi.popularmovies.di.Scopes;
import timber.log.Timber;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

/**
 * Class for maintaining global application state.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes());

        Scopes.getAppScope().installModules(new AppModule(this));
    }
}
