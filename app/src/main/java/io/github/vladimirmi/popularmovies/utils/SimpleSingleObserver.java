package io.github.vladimirmi.popularmovies.utils;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

public abstract class SimpleSingleObserver<T> extends DisposableSingleObserver<T> {

    @Override
    public abstract void onSuccess(T t);

    @Override
    public void onError(Throwable e) {
        Timber.e(e);
    }
}
