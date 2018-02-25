package io.github.vladimirmi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.github.vladimirmi.popularmovies.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * An activity representing a list of Movies.
 */
public class MovieListActivity extends AppCompatActivity {

    private MovieAdapter mAdapter;
    private CompositeDisposable mCompDisp = new CompositeDisposable();
    private Sort mSortBy;
    private boolean mSortByChanged = false;
    private PaginatedRecyclerViewScrollListener mPaginatedListener;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSpinner();
        setupRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompDisp.clear();
    }

    private final MovieAdapter.OnMovieClickListener mOnMovieClickListener = (itemView, movie) -> {
        Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.ARG_MOVIE, movie);

        String transitionName = getString(R.string.transition_name);
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(MovieListActivity.this,
                        itemView.findViewById(R.id.poster), transitionName);

        startActivity(intent, options.toBundle());
    };


    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateSpanCount());

        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(mOnMovieClickListener);
        mRecyclerView.setAdapter(mAdapter);


        mPaginatedListener = new PaginatedRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                fetchData(page);
            }
        };
        mRecyclerView.addOnScrollListener(mPaginatedListener);
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by, R.layout.item_spinner_sort_by);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(App.getDataManager().getSortBy().ordinal());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSortBy = Sort.values()[position];
                mSortByChanged = true;
                App.getDataManager().saveSortBy(mSortBy);
                mPaginatedListener.reset();
                mPaginatedListener.loadMore();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchData(int page) {
        switch (mSortBy) {
            case POPULAR:
                fetchPopularMovies(page);
                break;
            case TOP_RATED:
                fetchTopRatedMovies(page);
                break;
        }
    }

    private void fetchPopularMovies(int page) {
        mCompDisp.clear();
        mCompDisp.add(App.getDataManager().getPopularMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<PaginatedMoviesResult>() {
                    @Override
                    public void onSuccess(PaginatedMoviesResult paginatedMoviesResult) {
                        setData(paginatedMoviesResult);
                    }
                }));
    }

    private void fetchTopRatedMovies(int page) {
        mCompDisp.clear();
        mCompDisp.add(App.getDataManager().getTopRatedMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<PaginatedMoviesResult>() {
                    @Override
                    public void onSuccess(PaginatedMoviesResult paginatedMoviesResult) {
                        setData(paginatedMoviesResult);
                    }
                }));
    }

    private void setData(PaginatedMoviesResult paginatedMoviesResult) {
        if (mSortByChanged) {
            mRecyclerView.scrollToPosition(0);
            mAdapter.resetData();
            mSortByChanged = false;
        }
        mAdapter.addData(paginatedMoviesResult.getResults());
    }

    private int calculateSpanCount() {
        final int maxPosterWidth = (int) getResources().getDimension(R.dimen.poster_max_width);

        int spanCount = 0;
        DisplayMetrics displayMetrics = Utils.getDisplayMetrics(this);
        int posterWidthDp;
        do {
            spanCount++;
            posterWidthDp = displayMetrics.widthPixels / spanCount;
        } while (posterWidthDp >= maxPosterWidth);

        return spanCount;
    }

    public enum Sort {
        POPULAR, TOP_RATED
    }
}
