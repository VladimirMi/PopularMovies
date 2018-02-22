package io.github.vladimirmi.popularmovies.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import io.github.vladimirmi.popularmovies.App;
import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.PaginatedMoviesResult;
import io.github.vladimirmi.popularmovies.moviedetail.MovieDetailActivity;
import io.github.vladimirmi.popularmovies.moviedetail.MovieDetailFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compDisp.dispose();
    }

    private void fetchData() {
        DisposableSingleObserver<PaginatedMoviesResult> disposable =
                App.getDataManager().getPopularMovies(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaginatedMoviesResult>() {
                            @Override
                            public void onSuccess(PaginatedMoviesResult paginatedMoviesResult) {
                                mAdapter.setData(paginatedMoviesResult.getResults());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                            }
                        });
        compDisp.add(disposable);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.movie_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MovieAdapter(mListener);
        recyclerView.setAdapter(mAdapter);
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
}
