package io.github.vladimirmi.popularmovies.presentation.moviedetails.view;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.data.net.Api;
import io.github.vladimirmi.popularmovies.di.Scopes;
import io.github.vladimirmi.popularmovies.presentation.core.BaseFragment;
import io.github.vladimirmi.popularmovies.presentation.moviedetails.MovieDetailsPresenter;
import io.github.vladimirmi.popularmovies.presentation.moviedetails.MovieDetailsView;
import io.github.vladimirmi.popularmovies.presentation.movielist.view.MovieListActivity;
import io.github.vladimirmi.popularmovies.utils.AspectRatioImageView;
import io.github.vladimirmi.popularmovies.utils.FabVisibleListener;
import io.github.vladimirmi.popularmovies.utils.PaginatedRecyclerViewScrollListener;
import io.github.vladimirmi.popularmovies.utils.Utils;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.config.Module;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailsActivity}
 * on handsets.
 */

@SuppressWarnings("WeakerAccess")
public class MovieDetailsFragment extends BaseFragment<MovieDetailsPresenter, MovieDetailsView>
        implements MovieDetailsView {


    public static final String ARG_MOVIE = "movie_key";
    public static final String ARG_TWO_PANE = "two_pane_key";

    @BindView(R.id.app_bar) AppBarLayout mAppbar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.poster) AspectRatioImageView mPoster;
    @BindView(R.id.movie_title) TextView mTitle;
    @BindView(R.id.movie_rating) TextView mRating;
    @BindView(R.id.movie_release) TextView mRelease;
    @BindView(R.id.movie_overview) TextView mOverview;
    @BindView(R.id.trailers_label) TextView mTrailersLabel;
    @BindView(R.id.movie_trailers) LinearLayout mTrailers;
    @BindView(R.id.reviews_label) TextView mReviewsLabel;
    @BindView(R.id.movie_reviews) RecyclerView mReviews;
    @BindView(R.id.trailers_scroll) HorizontalScrollView mTrailersScroll;
    @BindView(R.id.like_btn) FloatingActionButton mLikeBtn;

    private boolean mTwoPane;
    private ReviewsAdapter mReviewsAdapter;
    private int mScopeName;
    private boolean mIsFabVisible;
    private boolean mIsFavorite;

    public MovieDetailsFragment() {
    }

    @Override
    protected int getLayout() {
        return R.layout.view_movie_details;
    }

    @Override
    protected MovieDetailsPresenter providePresenter() {
        mTwoPane = getArguments().containsKey(ARG_TWO_PANE);
        Movie movie = getArguments().getParcelable(ARG_MOVIE);
        mScopeName = movie.getId();
        Scope scope = Toothpick.openScopes(Scopes.APP_SCOPE, mScopeName);
        scope.installModules(new Module() {{
            bind(MovieDetailsPresenter.class).singletonInScope();
        }});
        MovieDetailsPresenter presenter = scope.getInstance(MovieDetailsPresenter.class);
        presenter.setMovie(movie);
        return presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity().isFinishing()) {
            Toothpick.closeScope(mScopeName);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mIsFabVisible) {
            if (mIsFavorite) {
                inflater.inflate(R.menu.favorite_menu, menu);
            } else {
                inflater.inflate(R.menu.not_favorite_menu, menu);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                mPresenter.switchFavorite();
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupView(View view) {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mReviews.setLayoutManager(lm);
        mReviewsAdapter = new ReviewsAdapter();
        mReviews.setAdapter(mReviewsAdapter);
        mReviews.setNestedScrollingEnabled(false);
        mReviews.addOnScrollListener(new PaginatedRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page) {
                mPresenter.loadMoreReviews(page);
            }
        });

        mLikeBtn.setOnClickListener(v -> mPresenter.switchFavorite());

        mAppbar.addOnOffsetChangedListener(new FabVisibleListener() {
            @Override
            public void onVisibleChanged(boolean isVisible) {
                mIsFabVisible = isVisible;
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setMovie(Movie movie) {
        Utils.setImage(mPoster, movie.getBackdropUrl(Api.BackdropSize.MID));
        mTitle.setText(movie.getTitle());
        mRating.setText(String.format("%1.1f / 10", movie.getVoteAverage()));
        mRelease.setText(Utils.formatDate(movie.getReleaseDate()));
        mOverview.setText(movie.getOverview());
        setupToolbar(movie);
    }

    @Override
    public void setIsFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
        getActivity().invalidateOptionsMenu();
        if (isFavorite) {
            mLikeBtn.setImageResource(R.drawable.ic_favorite);
        } else {
            mLikeBtn.setImageResource(R.drawable.ic_not_favorite);
        }
    }

    @Override
    public void setTrailers(List<Video> videos) {
        if (videos.isEmpty()) return;

        mTrailersLabel.setVisibility(View.VISIBLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mTrailers.removeAllViews();
        for (Video video : videos) {
            View thumbContainer = inflater.inflate(R.layout.item_video_thumbnail, mTrailers, false);
            ImageView thumb = thumbContainer.findViewById(R.id.thumb);
            Utils.setImage(thumb, video.getThumbnailUrl());
            mTrailers.addView(thumbContainer);
            thumb.setOnClickListener(v -> playOnYouTube(video.getKey(), video.getUrl()));
        }
        Utils.runAfterMeasure(mTrailersScroll, () -> {
            if (!Utils.canScrollHorizontal(mTrailersScroll)) {
                HorizontalScrollView.LayoutParams lp = (HorizontalScrollView.LayoutParams)
                        mTrailers.getLayoutParams();
                lp.gravity = Gravity.CENTER;
                mTrailers.setGravity(Gravity.CENTER);
                mTrailers.setLayoutParams(lp);
            }
        });
    }

    @Override
    public void setReviews(List<Review> reviews) {
        if (!reviews.isEmpty()) {
            mReviewsLabel.setVisibility(View.VISIBLE);
            mReviewsAdapter.setData(reviews);
        }
    }

    @Override
    public void showSnack(int stringResId) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                stringResId, Snackbar.LENGTH_SHORT).show();
    }

    private void setupToolbar(Movie movie) {
        if (mTwoPane) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setTitle(movie.getTitle());
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(mToolbar);

            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    private void playOnYouTube(String key, String url) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            getActivity().startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            getActivity().startActivity(webIntent);
        }
    }
}
