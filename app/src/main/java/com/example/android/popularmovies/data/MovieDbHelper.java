package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;


public class MovieDbHelper extends SQLiteOpenHelper {
    /*
   * This is the name of our database. Database names should be descriptive and end with the
   * .db extension.
   */
    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache movie data.
         */
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "                     +

                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"                  +

                        MovieEntry.COLUMN_POSTER   + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_RATINGS  + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_PLOT   + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL "                    +

                ");";


        //Second Table

        final String SQL_CREATE_MOVIE_FAVOURITE_TABLE =

                "CREATE TABLE " + MovieEntry.FAVORITE_TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "                     +

                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"                  +

                        MovieEntry.COLUMN_POSTER   + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_RATINGS  + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_PLOT   + " TEXT NOT NULL, "                    +

                        MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL, "                    +

                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVOURITE_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.FAVORITE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
