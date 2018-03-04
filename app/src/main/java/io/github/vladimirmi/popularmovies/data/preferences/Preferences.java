package io.github.vladimirmi.popularmovies.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.vladimirmi.popularmovies.data.entity.Sort;

public class Preferences {

    private static final String KEY_SORT_BY = "SORT_BY";
    public final Preference<String> sortByOrderPref;

    public Preferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("default", Context.MODE_PRIVATE);

        sortByOrderPref = new Preference<>(prefs, KEY_SORT_BY, Sort.POPULAR.name());
    }
}
