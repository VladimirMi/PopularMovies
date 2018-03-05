package io.github.vladimirmi.popularmovies.utils;


import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposables;


/**
 * Created by Vladimir Mikhalev 05.03.2018.
 */

public class ContentObservable {

    public static Observable<Cursor> create(ContentResolver resolver, Uri uri) {
        HandlerThread handlerThread = new HandlerThread("ContentObservableThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        Scheduler scheduler = AndroidSchedulers.from(handlerThread.getLooper());

        return Observable.create((ObservableOnSubscribe<Cursor>) emitter -> {
            final Cursor[] cursor = {resolver.query(uri, null, null, null, null)};
            emitter.onNext(cursor[0]);

            ContentObserver observer = new ContentObserver(handler) {
                @Override
                public void onChange(boolean selfChange) {
                    if (!emitter.isDisposed()) {
                        cursor[0].close();
                        cursor[0] = resolver.query(uri, null, null, null, null);
                        emitter.onNext(cursor[0]);
                    }
                }
            };

            resolver.registerContentObserver(uri, false, observer);

            emitter.setDisposable(Disposables.fromRunnable(() -> {
                cursor[0].close();
                resolver.unregisterContentObserver(observer);
                handlerThread.quit();
            }));
        }).subscribeOn(scheduler);
    }

    private ContentObservable() {
    }
}
