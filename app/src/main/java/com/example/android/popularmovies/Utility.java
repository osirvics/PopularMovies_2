package com.example.android.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Victor on 4/4/2017.
 */

public class Utility {
    public static final String DEESCENDING_POPULARITY = "popular";
    public static final String VOTE_AVERAGE = "top_rated";
    public static final String SORT_ORDER = "sort_order";

    public static String getSortOrder(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SORT_ORDER, DEESCENDING_POPULARITY);
    }
    public static void setSortOrder(Context context, String input) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SORT_ORDER, input)
                .apply();
    }

}
