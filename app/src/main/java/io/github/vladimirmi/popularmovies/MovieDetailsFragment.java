package io.github.vladimirmi.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.data.entity.Video;
import io.github.vladimirmi.popularmovies.data.net.Api;
import io.github.vladimirmi.popularmovies.utils.SimpleSingleObserver;
import io.github.vladimirmi.popularmovies.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    private ReviewsAdapter mReviewsAdapter;

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
        Utils.setImage(poster, mMovie.getBackdropUrl(Api.BackdropSize.MID));

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
        setupToolbar();
        setupTrailers();
        setupReviews();
    }

    private void setupToolbar() {
        if (mTwoPane) {
            getView().findViewById(R.id.toolbar).setVisibility(View.GONE);
        } else {
            Toolbar toolbar = getView().findViewById(R.id.toolbar);
            toolbar.setTitle(mMovie.getTitle());
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);

            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupTrailers() {
        App.getDataManager().getTrailers(String.valueOf(mMovie.getId()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Video>>() {
                    @Override
                    public void onSuccess(List<Video> videos) {
                        if (videos.isEmpty()) {
                            getView().findViewById(R.id.trailers_label).setVisibility(View.GONE);
                            return;
                        }
                        LinearLayout trailersContainer = getView().findViewById(R.id.movie_trailers);

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        for (Video video : videos) {
                            View thumbContainer = inflater.inflate(R.layout.view_video_thumbnail, trailersContainer, false);
                            ImageView thumb = thumbContainer.findViewById(R.id.thumb);
                            Utils.setImage(thumb, video.getThumbnailUrl());
                            trailersContainer.addView(thumbContainer);
                            thumb.setOnClickListener(v -> playOnYouTube(video.getKey(), video.getUrl()));
                        }

                        //todo if can scroll don't
                        HorizontalScrollView.LayoutParams lp = (HorizontalScrollView.LayoutParams) trailersContainer.getLayoutParams();
                        lp.gravity = Gravity.CENTER;
                        trailersContainer.setGravity(Gravity.CENTER);
                        trailersContainer.setLayoutParams(lp);
                    }
                });
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

    private void setupReviews() {
        RecyclerView reviewsRecycler = getView().findViewById(R.id.movie_reviews);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        reviewsRecycler.setLayoutManager(lm);
        mReviewsAdapter = new ReviewsAdapter();
        reviewsRecycler.setAdapter(mReviewsAdapter);
        reviewsRecycler.setNestedScrollingEnabled(false);
        reviewsRecycler.addOnScrollListener(new PaginatedRecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page) {
                fetchReviews(page);
            }
        });
        fetchReviews(1);
    }

    private void fetchReviews(int page) {
        App.getDataManager().getReviwes(String.valueOf(mMovie.getId()), page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SimpleSingleObserver<List<Review>>() {
                    @Override
                    public void onSuccess(List<Review> reviews) {
                        if (reviews.isEmpty()) {
                            getView().findViewById(R.id.reviews_label).setVisibility(View.GONE);
                        } else {
                            mReviewsAdapter.addData(reviews);
                        }
                    }
                });
    }
}
