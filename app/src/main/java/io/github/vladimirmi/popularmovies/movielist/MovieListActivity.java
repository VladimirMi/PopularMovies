package io.github.vladimirmi.popularmovies.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.core.BaseActivity;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.di.Scopes;
import io.github.vladimirmi.popularmovies.moviedetails.MovieDetailsActivity;
import io.github.vladimirmi.popularmovies.moviedetails.MovieDetailsFragment;
import io.github.vladimirmi.popularmovies.utils.PaginatedRecyclerViewScrollListener;
import io.github.vladimirmi.popularmovies.utils.Utils;
import timber.log.Timber;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailsActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */

public class MovieListActivity extends BaseActivity<MovieListPresenter, MovieListView>
        implements MovieListView {

    @BindView(R.id.toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.sort_by_spinner) Spinner mSpinner;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.movie_list) RecyclerView mMovieList;


    private MovieAdapter mMovieAdapter;
    private boolean mTwoPane;
    private boolean mIsSortChanged = false;
    private PaginatedRecyclerViewScrollListener mPaginatedListener;

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

    private final AdapterView.OnItemSelectedListener mOnSortChangeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.saveSortByPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_movie_list;
    }

    @Override
    protected MovieListPresenter providePresenter() {
        return Scopes.getAppScope().getInstance(MovieListPresenter.class);
    }

    @Override
    protected void setupView() {
        mTwoPane = findViewById(R.id.movie_details_container) != null;
        setSupportActionBar(mToolbar);
        setupSpinner();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        GridLayoutManager lm = new GridLayoutManager(this, calculateSpanCount());
        mMovieList.setLayoutManager(lm);
        mMovieAdapter = new MovieAdapter(mOnMovieClickListener, mTwoPane);
        mMovieList.setAdapter(mMovieAdapter);
        mPaginatedListener = new PaginatedRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page) {
                mPresenter.fetchMovies(page);
            }
        };
        mMovieList.addOnScrollListener(mPaginatedListener);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void setMovies(List<Movie> movies) {
        if (mIsSortChanged) {
            mMovieList.scrollToPosition(0);
            mMovieAdapter.resetData();
            mIsSortChanged = false;
        }
        mMovieAdapter.setData(movies);
    }

    @Override
    public void setSortByPosition(int position) {
        mSpinner.setOnItemSelectedListener(null);
        mSpinner.setSelection(position);
        mSpinner.setOnItemSelectedListener(mOnSortChangeListener);
    }

    @Override
    public void resetMoviesList() {
        mPaginatedListener.reset();
        mIsSortChanged = true;
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
}
