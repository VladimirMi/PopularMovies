package io.github.vladimirmi.popularmovies.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
}
