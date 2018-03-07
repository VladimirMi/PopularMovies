package io.github.vladimirmi.popularmovies.di;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import io.github.vladimirmi.popularmovies.data.DataRepositoryImpl;
import io.github.vladimirmi.popularmovies.data.db.MovieDbHelper;
import io.github.vladimirmi.popularmovies.data.net.NetworkChecker;
import io.github.vladimirmi.popularmovies.data.net.RestService;
import io.github.vladimirmi.popularmovies.data.net.RestServiceProvider;
import io.github.vladimirmi.popularmovies.data.preferences.Preferences;
import io.github.vladimirmi.popularmovies.domain.DataRepository;
import io.github.vladimirmi.popularmovies.domain.MovieDetailInteractorImpl;
import io.github.vladimirmi.popularmovies.domain.MovieListInteractorImpl;
import io.github.vladimirmi.popularmovies.presentation.moviedetails.MovieDetailInteractor;
import io.github.vladimirmi.popularmovies.presentation.movielist.MovieListInteractor;
import io.github.vladimirmi.popularmovies.presentation.movielist.MovieListPresenter;
import toothpick.config.Module;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public class AppModule extends Module {

    public AppModule(Context context) {

        bind(RestService.class).toInstance(RestServiceProvider.getService());
        bind(Preferences.class).toInstance(new Preferences(context));
        bind(NetworkChecker.class).toInstance(new NetworkChecker(context));
        bind(SQLiteDatabase.class).toInstance(new MovieDbHelper(context).getWritableDatabase());
        bind(ContentResolver.class).toInstance(context.getContentResolver());

        bind(DataRepository.class).to(DataRepositoryImpl.class).singletonInScope();
        bind(MovieDetailInteractor.class).to(MovieDetailInteractorImpl.class);
        bind(MovieListInteractor.class).to(MovieListInteractorImpl.class);
        bind(MovieListPresenter.class).singletonInScope();
    }
}
