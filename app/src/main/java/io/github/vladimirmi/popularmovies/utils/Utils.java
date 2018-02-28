package io.github.vladimirmi.popularmovies.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Vladimir Mikhalev 22.02.2018.
 */

public class Utils {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/%s/%s";

    public enum PosterQuality {
        ORIGINAL("original"),
        VERY_HIGH("w780"),
        HIGH("w500"),
        MID("w342"),
        LOW("w154"),
        VERY_LOW("w92");

        final String path;

        PosterQuality(String path) {
            this.path = path;
        }
    }

    @SuppressLint("DefaultLocale")
    private static String getImagePath(PosterQuality quality, String path) {
        return String.format(BASE_URL, quality.path, path);
    }

    public static void setImage(ImageView view, String path, PosterQuality quality) {
        Glide.with(view.getContext())
                .load(getImagePath(quality, path))
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .into(view);
    }

    @SuppressWarnings("ConstantConditions")
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static String formatDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String formattedDate;
        try {
            Date d = format.parse(date);
            formattedDate = DateFormat.getDateInstance().format(d);
        } catch (ParseException e) {
            formattedDate = date;
        }
        return formattedDate;
    }

    public static boolean canScroll(NestedScrollView scrollView) {
        View child = scrollView.getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            Timber.e("canScroll: " + childHeight);
            Timber.e("canScroll: " + scrollView.getHeight());
            return scrollView.getHeight() < childHeight + scrollView.getPaddingTop() + scrollView.getPaddingBottom();
        }
        return false;
    }
}
