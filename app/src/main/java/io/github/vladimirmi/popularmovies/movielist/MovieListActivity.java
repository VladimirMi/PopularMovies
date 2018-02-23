package io.github.vladimirmi.popularmovies.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.github.vladimirmi.popularmovies.App;
import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.moviedetail.MovieDetailActivity;
import io.github.vladimirmi.popularmovies.moviedetail.MovieDetailFragment;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MovieAdapter mAdapter;
    private CompositeDisposable compDisp = new CompositeDisposable();
    private Sort sortBy = Sort.POPULAR;
    private boolean sortChanged = false;
    private PaginatedRecyclerViewScrollListener mPaginatedRecyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSpinner();

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compDisp.dispose();
    }

    private final MovieAdapter.OnMovieClickListener mListener = new MovieAdapter.OnMovieClickListener() {
        @Override
        public void onMovieClick(Movie movie) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, movie);
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_MOVIE, movie);

                startActivity(intent);
            }
        }
    };


    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(mListener);
        recyclerView.setAdapter(mAdapter);

        mPaginatedRecyclerViewScrollListener = new PaginatedRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                fetchData(page);
            }
        };
        recyclerView.addOnScrollListener(mPaginatedRecyclerViewScrollListener);
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.sort_by_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = Sort.values()[position];
                sortChanged = true;
                mPaginatedRecyclerViewScrollListener.reset();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchData(int page) {
        if (sortBy == Sort.POPULAR) {
            fetchPopularMovies(page);
        } else if (sortBy == Sort.TOP_RATED) {
            fetchTopRatedMovies(page);
        }
    }

    private void fetchPopularMovies(int page) {
        compDisp.dispose();
        compDisp.add(App.getDataManager().getPopularMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<PaginatedMoviesResult>() {
                    @Override
                    public void onSuccess(PaginatedMoviesResult paginatedMoviesResult) {
                        setData(paginatedMoviesResult);
                    }
                }));
    }

    private void fetchTopRatedMovies(int page) {
        compDisp.dispose();
        compDisp.add(App.getDataManager().getTopRatedMovies(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<PaginatedMoviesResult>() {
                    @Override
                    public void onSuccess(PaginatedMoviesResult paginatedMoviesResult) {
                        setData(paginatedMoviesResult);
                    }
                }));
    }

    private void setData(PaginatedMoviesResult paginatedMoviesResult) {
        if (sortChanged) {
            mAdapter.resetData();
            sortChanged = false;
        }
        mAdapter.addData(paginatedMoviesResult.getResults());
    }

    enum Sort {
        POPULAR, TOP_RATED
    }
}
