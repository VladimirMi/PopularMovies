package io.github.vladimirmi.popularmovies.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * {@link RecyclerView.OnScrollListener}, witch handles pagination by calling
 * {@link #onLoadMore(int)}} when from the last visible item to end of the list,
 * less then half of page size is left.
 * <p>
 * Default page size is 20.
 */

@SuppressWarnings("WeakerAccess")
public abstract class PaginatedRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int PAGE_SIZE = 20;
    private static final int THRESHOLD = PAGE_SIZE / 2;

    private int mLoadedPages = 0;
    private int mPreviousItemCount = 0;
    private boolean mLoading = true;

    private final LinearLayoutManager mLayoutManager;

    public PaginatedRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int itemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        mLoadedPages = itemCount / PAGE_SIZE + (itemCount % PAGE_SIZE == 0 ? 0 : 1);

        if (mLoading && itemCount > mPreviousItemCount) {
            mLoading = false;
            mPreviousItemCount = itemCount;
        }

        if (!mLoading && lastVisibleItemPosition + THRESHOLD > PAGE_SIZE * mLoadedPages) {
            mLoading = true;
            onLoadMore(++mLoadedPages);
        }
    }

    public void reset() {
        mLoadedPages = 0;
        mPreviousItemCount = 0;
        mLoading = true;
    }

    /**
     * Called when a new page needs to be loaded.
     *
     * @param page Number of the page to load.
     */
    public abstract void onLoadMore(int page);
}
