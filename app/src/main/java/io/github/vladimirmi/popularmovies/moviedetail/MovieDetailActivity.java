package io.github.vladimirmi.popularmovies.moviedetail;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.vladimirmi.popularmovies.R;
import io.github.vladimirmi.popularmovies.data.entity.Movie;
import io.github.vladimirmi.popularmovies.utils.Utils;

/**
 * An activity representing a single Movie detail screen.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String ARG_MOVIE = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) {
            Movie movie = getIntent().getParcelableExtra(MovieDetailActivity.ARG_MOVIE);
            ImageView poster = findViewById(R.id.poster);
            Utils.setImage(poster, movie.getPosterPath(), 500);

            TextView titleTv = findViewById(R.id.movie_title);
            titleTv.setText(movie.getTitle());
            TextView ratingTv = findViewById(R.id.movie_rating);
            ratingTv.setText(String.valueOf(movie.getVoteAverage()));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(movie.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView releaseTv = findViewById(R.id.movie_release);
            releaseTv.setText(DateFormat.getDateInstance().format(date));
            TextView overviewTv = findViewById(R.id.movie_overview);
            overviewTv.setText(movie.getOverview());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
