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
 * Created by Vladimir Mikhalev 21.02.2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovies = new ArrayList<>();
    private final OnMovieClickListener mListener;

    MovieAdapter(OnMovieClickListener clickListener) {
        mListener = clickListener;
    }

    public void setData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int spanCount = ((GridLayoutManager) ((RecyclerView) parent).getLayoutManager()).getSpanCount();
        lp.width = Utils.getDisplayMetrics(parent.getContext()).widthPixels / spanCount;
        lp.height = (int) (lp.width * 1.5);
        view.setLayoutParams(lp);
        return new MovieViewHolder(view);
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
