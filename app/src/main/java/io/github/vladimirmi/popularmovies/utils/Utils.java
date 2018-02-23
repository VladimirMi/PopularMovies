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

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w%d/%s";

    @SuppressLint("DefaultLocale")
    private static String getImagePath(int width, String path) {
        return String.format(BASE_URL, width, path);
    }

    public static void setImage(ImageView view, String path, int width) {
        Glide.with(view.getContext())
                .load(getImagePath(width, path))
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
