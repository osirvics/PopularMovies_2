package com.example.android.popularmovies.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

import org.json.JSONException;

import java.util.ArrayList;


public class PersistData {


   private static String TAG = "PersistData";

    synchronized public static void cacheFavoriteMovie(Context context, Results data){

    }

    synchronized public static void cacheData(Context context, ArrayList<Results> data) {

    }

    public static ContentValues[] getMovieContentValuesFromArray(Context context, ArrayList<Results> result)
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

    public static synchronized void cacheOfflineData(final Context context, final ArrayList<Results> data) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                try {

                    ContentValues[] movieValues =
                            getMovieContentValuesFromArray(context, data);
                    if ( movieValues != null &&  movieValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                        ContentResolver movieContentResolver = context.getContentResolver();
                /* Delete old movies data */
//                movieContentResolver.delete(
//                        MovieEntry.CONTENT_URI,
//                        null,
//                        null);
                /* Insert our new movie data into Movie's ContentProvider */
                        movieContentResolver.bulkInsert(
                                MovieEntry.CONTENT_URI,
                                movieValues);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }
        }.execute();

    }

    public static synchronized void cacheFavoriteMovieData(final Context context, final Results data) {
        new AsyncTask<Void, Void, Void>() {
            Uri uri = null;
            @Override
            protected Void doInBackground(final Void... unused) {
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
                    uri = context.getContentResolver().insert(MovieEntry.FAVORITE_CONTENT_URI, movieValues);
                    // Display the URI that's returned with a Toast


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error inserting data", Toast.LENGTH_LONG).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(uri != null) {
                    Toast.makeText(context, context.getString(R.string.added_as_favorite), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, context.getString(R.string.failed_to_add), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }
}
