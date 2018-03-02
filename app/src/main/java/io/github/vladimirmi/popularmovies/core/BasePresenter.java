package io.github.vladimirmi.popularmovies.core;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Vladimir Mikhalev 02.03.2018.
 */

public abstract class BasePresenter<V extends BaseView> {

    protected boolean isFirstAttach = true;
    protected V mView;
    protected CompositeDisposable mCompDisp = new CompositeDisposable();

    public final void attachView(V view) {
        this.mView = view;
        if (isFirstAttach) {
            onFirstAttach(view);
        }
        onAttach(view);
        isFirstAttach = false;
    }

    public final void detachView() {
        onDetach();
        mCompDisp.clear();
        mView = null;
    }

    protected abstract void onFirstAttach(V view);

    protected void onAttach(V view) {
    }

    protected void onDetach() {
    }
}
