package io.github.vladimirmi.popularmovies.presentation.moviedetails.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Review;
import io.github.vladimirmi.popularmovies.utils.Utils;

/**
 * Provides a binding from an {@link Review} data set to views that are displayed
 * within a {@link RecyclerView}.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> mReviews = new ArrayList<>();
    private int mExpandedPosition = -1;

    public void setData(List<Review> reviews) {
        int oldSize = mReviews.size();
        mReviews = reviews;
        notifyItemRangeChanged(oldSize, reviews.size());
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        holder.bind(mReviews.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView mAuthorView;
        private final TextView mContentView;
        private final Button mExpandCollapseBtn;

        ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorView = itemView.findViewById(R.id.review_author);
            mContentView = itemView.findViewById(R.id.review_content);
            mExpandCollapseBtn = itemView.findViewById(R.id.expand_collapse);
        }

        void bind(Review review, int position) {
            mAuthorView.setText(review.getAuthor());
            mContentView.setText(review.getContent());

            boolean isExpanded = position == mExpandedPosition;

            mContentView.setMaxLines(isExpanded ? 1000 : 10);
            mExpandCollapseBtn.setText(isExpanded ?
                    itemView.getContext().getString(R.string.collapse) :
                    itemView.getContext().getString(R.string.expand));

            Utils.runAfterMeasure(mContentView, () -> {
                if (mContentView.getLineCount() < 10) mExpandCollapseBtn.setVisibility(View.GONE);
            });

            mExpandCollapseBtn.setOnClickListener(v -> {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(position);
            });
        }
    }
}
