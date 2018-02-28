package io.github.vladimirmi.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.utils.Utils;
import timber.log.Timber;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailsActivity}
 * on handsets.
 */

public class MovieDetailsFragment extends Fragment {


    public static final String ARG_MOVIE = "movie_key";
    public static final String ARG_TWO_PANE = "two_pane_key";
    private Movie mMovie;
    private boolean mTwoPane;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMovie = getArguments().getParcelable(ARG_MOVIE);
        mTwoPane = getArguments().containsKey(ARG_TWO_PANE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_movie_details, container, false);

        if (mMovie == null) return rootView;

        ImageView poster = rootView.findViewById(R.id.poster);
        Utils.setImage(poster, mMovie.getBackdropPath(), Utils.PosterQuality.HIGH);

        TextView titleTv = rootView.findViewById(R.id.movie_title);
        titleTv.setText(mMovie.getTitle());

        TextView ratingTv = rootView.findViewById(R.id.movie_rating);
        ratingTv.setText(String.valueOf(mMovie.getVoteAverage()));

        TextView releaseTv = rootView.findViewById(R.id.movie_release);
        releaseTv.setText(Utils.formatDate(mMovie.getReleaseDate()));

        TextView overviewTv = rootView.findViewById(R.id.movie_overview);
        overviewTv.setText(mMovie.getOverview());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupToolbar(view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setupScrolling(view);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setupToolbar(View view) {
        if (mTwoPane) {
            view.findViewById(R.id.toolbar).setVisibility(View.GONE);
        } else {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.setTitle(mMovie.getTitle());
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);

            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupScrolling(View view) {
        NestedScrollView scroll = view.findViewById(R.id.movie_details_scroll);
        CollapsingToolbarLayout collapsing = view.findViewById(R.id.toolbar_layout);

        boolean canScroll = Utils.canScroll(scroll);
        Timber.e("onViewCreated: " + canScroll);
        int scrollFlags = 0;
        if (canScroll) {
            scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
        }
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) collapsing.getLayoutParams();
        lp.setScrollFlags(scrollFlags);
        collapsing.setLayoutParams(lp);
    }


}
