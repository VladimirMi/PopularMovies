package io.github.vladimirmi.popularmovies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.utils.Utils;

/**
 * Provides a binding from an {@link Movie} data set to views that are displayed
 * within a {@link RecyclerView}.
 */

@SuppressWarnings("WeakerAccess")
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final double POSTER_ASPECT_RATIO = 1.5;

    private final OnMovieClickListener mListener;
    private final boolean mTwoPane;
    private List<Movie> mMovies = new ArrayList<>();

    private int mPosterWidth = 0;
    private int mPosterHeight = 0;

    MovieAdapter(OnMovieClickListener clickListener, boolean twoPane) {
        mListener = clickListener;
        mTwoPane = twoPane;
    }

    public void addData(List<Movie> movies) {
        int oldSize = mMovies.size();
        mMovies.addAll(movies);
        notifyItemRangeChanged(oldSize, movies.size());
    }

    public void resetData() {
        mMovies = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (mPosterWidth == 0) {
            calculatePosterSize(parent);
        }
        lp.width = mPosterWidth;
        lp.height = mPosterHeight;
        view.setLayoutParams(lp);
        return new MovieViewHolder(view);
    }

    private void calculatePosterSize(ViewGroup parent) {
        int spanCount = ((GridLayoutManager) ((RecyclerView) parent).getLayoutManager()).getSpanCount();
        DisplayMetrics displayMetrics = Utils.getDisplayMetrics(parent.getContext());
        mPosterWidth = displayMetrics.widthPixels / spanCount;
        if (mTwoPane) mPosterWidth *= 0.4;
        mPosterHeight = (int) (mPosterWidth * POSTER_ASPECT_RATIO);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mPosterView;
        private final TextView mTitleView;
        private Movie mMovie;

        MovieViewHolder(View itemView) {
            super(itemView);
            mPosterView = itemView.findViewById(R.id.poster);
            mTitleView = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            mMovie = movie;
            mTitleView.setText(movie.getTitle());
            Utils.setImage(mPosterView, movie.getPosterPath(), Utils.PosterQuality.LOW);
        }

        @Override
        public void onClick(View v) {
            mListener.onMovieClick(itemView, mMovie);
        }
    }

    interface OnMovieClickListener {

        void onMovieClick(View itemView, Movie movie);
    }
}
