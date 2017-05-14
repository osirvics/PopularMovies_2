package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;



public class MovieContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movie";

    public static final String PATH_FAVORITE = "favorite_movie";

    /* Inner class that defines the table contents of the weather table */
    public static final class MovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        public static final Uri FAVORITE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        /* Used internally as the name of our movie table. */
        public static final String TABLE_NAME = "movie";

        public static final String FAVORITE_TABLE_NAME = "favorite_movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_PLOT = "plot";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_RATINGS = "ratings";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_FAVORITE = "favorite";


    }

}
