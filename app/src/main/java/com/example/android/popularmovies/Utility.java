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
    public static final String FAVORITE_MOVIES = "favorite";
    public static final String MOVIE_STATE_KEY = "movie_key";
    public static final boolean addLoadingFooter = true;
    public static final boolean shouldAddLoadingFooter = false;
    public static final int ID_MOVIE_LOADER = 44;
    public static final int ID_FAVOURITE_LOADER = 45;

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
