package io.github.vladimirmi.popularmovies.utils;

import android.support.design.widget.AppBarLayout;


/**
 * Created by Vladimir Mikhalev 04.03.2018.
 */

public abstract class FabVisibleListener implements AppBarLayout.OnOffsetChangedListener {

    private static final int HIDE_IF_LESS_THAN = 170; //px
    private int totalScrollRange;
    private boolean isVisible;


    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (totalScrollRange == 0) totalScrollRange = appBarLayout.getTotalScrollRange();
        if (totalScrollRange - Math.abs(i) <= HIDE_IF_LESS_THAN) {
            if (isVisible) {
                isVisible = false;
                onVisibleChanged(false);
            }
        } else {
            if (!isVisible) {
                isVisible = true;
                onVisibleChanged(true);
            }
        }
    }

    public abstract void onVisibleChanged(boolean isVisible);
}
