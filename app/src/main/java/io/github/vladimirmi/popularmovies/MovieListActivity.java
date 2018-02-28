package io.github.vladimirmi.popularmovies;

import android.content.Intent;
import android.os.Bundle;
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
import timber.log.Timber;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * An activity representing a list of Movies.
 */

public class MovieListActivity extends AppCompatActivity {

    private MovieAdapter mAdapter;
    private CompositeDisposable mCompDisp = new CompositeDisposable();
    private Sort mSortBy;
    private boolean mSortByChanged = false;
    private PaginatedRecyclerViewScrollListener mPaginatedListener;
    private RecyclerView mRecyclerView;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.movie_details_container) != null;

        setupSpinner();
        setupRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompDisp.clear();
    }

    private final MovieAdapter.OnMovieClickListener mOnMovieClickListener = (itemView, movie) -> {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            Timber.e(movie.toString());
            arguments.putParcelable(MovieDetailsFragment.ARG_MOVIE, movie);
            arguments.putBoolean(MovieDetailsFragment.ARG_TWO_PANE, true);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsFragment.ARG_MOVIE, movie);

            startActivity(intent);
        }
    };


    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateSpanCount());

        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(mOnMovieClickListener, mTwoPane);
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
                R.array.sort_by, android.R.layout.simple_spinner_item);
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
            if (mTwoPane) posterWidthDp *= 0.4;
        } while (posterWidthDp >= maxPosterWidth);

        return spanCount;
    }

    public enum Sort {
        POPULAR, TOP_RATED
    }
}
