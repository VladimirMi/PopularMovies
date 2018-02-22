package io.github.vladimirmi.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import io.github.vladimirmi.popularmovies.data.DataManager;
import io.github.vladimirmi.popularmovies.data.net.RestServiceProvider;
import timber.log.Timber;

/**
 * Class for maintaining global application state.
 */

public class App extends Application {

    private static DataManager sDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        sDataManager = new DataManager(RestServiceProvider.getService());
    }

    public static DataManager getDataManager() {
        return sDataManager;
    }
}
