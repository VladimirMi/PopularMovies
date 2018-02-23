package io.github.vladimirmi.popularmovies.movielist;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.utils.Utils;

/**
 * Provides a binding from an {@link Movie} data set to views that are displayed
 * within a {@link RecyclerView}.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final float POSTER_ASPECT_RATIO = 2f / 3f;
    private final OnMovieClickListener mListener;
    private List<Movie> mMovies = new ArrayList<>();

    private int mPosterWidth = 0;
    private int mPosterHeight = 0;

    MovieAdapter(OnMovieClickListener clickListener) {
        mListener = clickListener;
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
        mPosterWidth = Utils.getDisplayMetrics(parent.getContext()).widthPixels / spanCount;
        mPosterHeight = (int) (mPosterWidth / POSTER_ASPECT_RATIO);
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
        private Movie mMovie;

        MovieViewHolder(View itemView) {
            super(itemView);
            mPosterView = itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            mMovie = movie;
            Utils.setImage(mPosterView, movie.getPosterPath(), 154);
        }

        @Override
        public void onClick(View v) {
            mListener.onMovieClick(mMovie);
        }
    }

    interface OnMovieClickListener {

        void onMovieClick(Movie movie);
    }
}
