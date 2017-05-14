package com.example.android.popularmovies.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

import org.json.JSONException;

import java.util.ArrayList;


public class PersistData {


    synchronized public static void cacheFavoritedata(Context context, Results data){
        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object


        try {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, data.getId());
            movieValues.put(MovieEntry.COLUMN_TITLE, data.getTitle());
            movieValues.put(MovieEntry.COLUMN_POSTER, data.getPosterPath());
            movieValues.put(MovieEntry.COLUMN_RATINGS, data.getVoteAverage());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, data.getReleaseDate());
            movieValues.put(MovieEntry.COLUMN_PLOT, data.getOverview());
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);
            // Insert the content values via a ContentResolver
            Uri uri = context.getContentResolver().insert(MovieEntry.FAVORITE_CONTENT_URI, movieValues);

            // Display the URI that's returned with a Toast
            // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
            if(uri != null) {
                Toast.makeText(context, uri.toString() +"  Data inserted", Toast.LENGTH_LONG).show();
            }

            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(MovieEntry.FAVORITE_CONTENT_URI,
                        null,
                        null,
                        null, MovieEntry.COLUMN_RELEASE_DATE);

            } catch (Exception e) {
                //Log.e(TAG, "Failed to asynchronously load data.");
                e.printStackTrace();
                //return null;
            }
            Log.e("PersistData", "Size of favorite cursor: " + cursor.getCount() );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public static void cacheData(Context context, ArrayList<Results> data) {

        try {

            ContentValues[] movieValues =
                    getWeatherContentValuesFromArray(context, data);

            if ( movieValues != null &&  movieValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver movieContentResolver = context.getContentResolver();

                /* Delete old weather data because we don't need to keep multiple days' data */
                movieContentResolver.delete(
                        MovieEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new movie data into Movie's ContentProvider */
                movieContentResolver.bulkInsert(
                        MovieEntry.CONTENT_URI,
                        movieValues);

                   Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null, MovieEntry.COLUMN_RELEASE_DATE);

                } catch (Exception e) {
                    //Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    //return null;
                }
                Log.e("PersistData", "Size of cursor: " + cursor.getCount() );

            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

    public static ContentValues[] getWeatherContentValuesFromArray(Context context, ArrayList<Results> result)
            throws JSONException {

        ContentValues[] movieContentValues = new ContentValues[result.size()];
        for (int i = 0; i < result.size(); i++) {
            Results data = result.get(i);


            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, data.getId());
            movieValues.put(MovieEntry.COLUMN_TITLE, data.getTitle());
            movieValues.put(MovieEntry.COLUMN_POSTER, data.getPosterPath());
            movieValues.put(MovieEntry.COLUMN_RATINGS, data.getVoteAverage());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, data.getReleaseDate());
            movieValues.put(MovieEntry.COLUMN_PLOT, data.getOverview());
            movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }
}
