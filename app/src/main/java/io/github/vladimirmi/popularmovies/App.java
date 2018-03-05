package io.github.vladimirmi.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

import timber.log.Timber;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;

import static toothpick.registries.FactoryRegistryLocator.setRootRegistry;
import static toothpick.registries.MemberInjectorRegistryLocator.setRootRegistry;

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

        if (BuildConfig.DEBUG) {
            Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes());
        } else {
            Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
            setRootRegistry(new FactoryRegistry());
            setRootRegistry(new MemberInjectorRegistry());
        }

        // moved to MovieContentProvider#onCreate() since there fires earlier
//        Scopes.getAppScope().installModules(new AppModule(this));
    }
}
