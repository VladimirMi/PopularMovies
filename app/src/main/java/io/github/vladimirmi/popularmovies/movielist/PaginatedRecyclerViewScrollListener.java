package io.github.vladimirmi.popularmovies.movielist;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * {@link RecyclerView.OnScrollListener}, witch handles pagination by calling
 * {@link #onLoadMore(int)}} when from the last visible item to end of the list,
 * less then half of page size is left.
 * <p>
 * Default page size is 20.
 */

abstract class PaginatedRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int PAGE_SIZE = 20;
    private static final int THRESHOLD = PAGE_SIZE / 2;

    private int mLoadedPages = 0;
    private int mPreviousItemCount = 0;
    private boolean mLoading = false;

    private GridLayoutManager mLayoutManager;

    public PaginatedRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
        mPreviousItemCount = mLayoutManager.getItemCount();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int itemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        if (mLoading && itemCount > mPreviousItemCount) {
            mLoading = false;
            mPreviousItemCount = itemCount;
        }

        if (!mLoading && lastVisibleItemPosition + THRESHOLD > PAGE_SIZE * mLoadedPages) {
            loadMore();
        }
    }

    public void loadMore() {
        mLoading = true;
        onLoadMore(++mLoadedPages);
    }

    public void reset() {
        mLoadedPages = 0;
        mPreviousItemCount = 0;
        mLoading = false;
    }

    /**
     * Called when a new page needs to be loaded.
     *
     * @param page Number of the page to load.
     */
    public abstract void onLoadMore(int page);
}
