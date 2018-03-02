package io.github.vladimirmi.popularmovies.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static void setImage(ImageView view, String path) {
        Glide.with(view.getContext())
                .load(path)
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

    public static boolean canScrollHorizontal(ViewGroup viewGroup) {
        View child = viewGroup.getChildAt(0);

        if (child != null) {
            int childWidth = child.getWidth();
            return viewGroup.getWidth() < childWidth + viewGroup.getPaddingLeft()
                    + viewGroup.getPaddingRight();
        }
        return false;
    }

    public static void runAfterMeasure(View view, Runnable runnable) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        runnable.run();
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }
}
