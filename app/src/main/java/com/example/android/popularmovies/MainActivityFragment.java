package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.api.OnItemClickListener;
import com.example.android.popularmovies.api.PaginationAdapterCallback;
import com.example.android.popularmovies.api.TheMoviedbApiService;
import com.example.android.popularmovies.api.TheMoviedbClient;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieDbHelper;
import com.example.android.popularmovies.model.Movies;
import com.example.android.popularmovies.model.PersistData;
import com.example.android.popularmovies.model.Results;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.api.TheMoviedbClient.getClient;

public class MainActivityFragment extends Fragment implements PaginationAdapterCallback, OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int PER_PAGE = 20;
    private static final int PAGE_START = 1;
    private static String TAG = "PersistData";
    int pageLoadCount = 0;
    ArrayList<Results> movieList;
    private RecyclerView grid;
    private ProgressBar empty;
    private MovieAdapter adapter;
    private LinearLayout errorLayout;
    private ArrayList<Results> movies;
    private Movies items;
    private Button btnRetry;
    private TextView txtError;
    //Indicates if footer ProgressBar is shown
    private boolean isLoading = false;
    private boolean isLastPage = false;
    //setting initial value to ten
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;
    //Default movie sort order
    private String sortOrder = Utility.DEESCENDING_POPULARITY;
    private MainActivityFragment context;
    private SQLiteDatabase mDb;

    View.OnClickListener retry = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideErrorView();
            displayData(sortOrder);
        }
    });

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        setupView(view);
        context = this;
        btnRetry.setOnClickListener(retry);
        sortOrder = Utility.getSortOrder(getActivity());
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        mDb = dbHelper.getWritableDatabase();


        if (savedInstanceState != null) {
            movies = savedInstanceState.getParcelableArrayList(Utility.MOVIE_STATE_KEY);
        }
        if (sortOrder.contains(Utility.FAVORITE_MOVIES)) {
            getActivity().getSupportLoaderManager().initLoader(Utility.ID_FAVOURITE_LOADER, null, context);
        } else {
            displayData(sortOrder);
        }

        return view;
    }

    private void populateGrid(ArrayList<Results> moviess, boolean flag) {
        adapter = new MovieAdapter(moviess, Glide.with(getActivity()), getActivity(), this, this);
        grid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        empty.setVisibility(View.GONE);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter(flag);
    }

    public void setupView(View view) {
        grid = (RecyclerView) view.findViewById(R.id.image_grid);
        empty = (ProgressBar) view.findViewById(R.id.empty);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        btnRetry = (Button) view.findViewById(R.id.error_btn_retry);
        txtError = (TextView) view.findViewById(R.id.error_txt_cause);
        final int columns = getResources().getInteger(R.integer.gallery_columns);
        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), columns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case MovieAdapter.ITEM:
                        return 1;
                    case MovieAdapter.LOADING:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        grid.setLayoutManager(gridLayoutManager);
        grid.addItemDecoration(new GridMarginDecoration(
                getResources().getDimensionPixelSize(R.dimen.grid_item_spacing)));

        grid.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1; //Increment page index to load the next one
                loadNextPage(sortOrder);

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Utility.MOVIE_STATE_KEY, movies);
        super.onSaveInstanceState(outState);

    }

    private void displayData(String sortBy) {
        if (movies != null) {
            // shouldAddFooter =false;
            populateGrid(movies, Utility.shouldAddLoadingFooter);

        } else {
            TheMoviedbApiService apiService =
                    getClient().create(TheMoviedbApiService.class);
            Call<Movies> call;
            if (sortBy.contains(Utility.DEESCENDING_POPULARITY)) {
                call = apiService.getPopularMovies(BuildConfig.MOVIE_API_KEY, currentPage);
            } else {
                call = apiService.getTopRatedMovies(BuildConfig.MOVIE_API_KEY, currentPage);
            }
            call.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                    if (response.code() == 200) {
                        items = new Movies();
                        items = response.body();
                        movies = new ArrayList<>();
                        movies = items.getResults();
                        populateGrid(movies, Utility.addLoadingFooter);
                        int totalCount = items.getTotalPages();
                        if (totalCount % PER_PAGE == 0)
                            TOTAL_PAGES = (totalCount / PER_PAGE);
                        else TOTAL_PAGES = (totalCount / PER_PAGE) + 1;
                        PersistData.cacheOfflineData(getActivity(), movies);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                    getActivity().getSupportLoaderManager().initLoader(Utility.ID_MOVIE_LOADER, null, context);
                    // t.printStackTrace();
                    //showErrorView(t);
                }
            });
        }
    }

    private void loadNextPage(String sortBy) {
        pageLoadCount += 1;
        TheMoviedbApiService apiService =
                TheMoviedbClient.getClient().create(TheMoviedbApiService.class);
        Call<Movies> call;
        if (sortBy.contains(Utility.DEESCENDING_POPULARITY)) {
            call = apiService.getPopularMovies(BuildConfig.MOVIE_API_KEY, currentPage);
        } else {
            call = apiService.getTopRatedMovies(BuildConfig.MOVIE_API_KEY, currentPage);
        }
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {

                if (response.code() == 200) {
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    final Movies items = response.body();
                    final ArrayList<Results> results = items != null ? items.getResults() : null;
                    adapter.addAll(results);
                    adapter.notifyDataSetChanged();
                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter(true);
                    else isLastPage = true;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     */
    private void showErrorView(Throwable throwable) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }
        return errorMsg;
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        if (movies != null)
            intent.putExtra("data", movies.get(position));
        else intent.putExtra("data", movieList.get(position));
        startActivity(intent);
    }


    @Override
    public void retryPageLoad() {
        loadNextPage(sortOrder);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item;
        if (sortOrder.contains(Utility.DEESCENDING_POPULARITY)) {
            item = menu.findItem(R.id.menu_sort_by_asc);
            item.setChecked(true);
        } else if (sortOrder.contains(Utility.VOTE_AVERAGE)) {
            item = menu.findItem(R.id.menu_sort_by_vote_average);
            item.setChecked(true);
        } else if (sortOrder.contains(Utility.FAVORITE_MOVIES)) {
            item = menu.findItem(R.id.menu_sort_by_favorite);
            item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_asc:
                reloadAndUpdateSort(getActivity(), Utility.DEESCENDING_POPULARITY, item);
                return true;
            case R.id.menu_sort_by_vote_average:
                reloadAndUpdateSort(getActivity(), Utility.VOTE_AVERAGE, item);
                return true;
            case R.id.menu_sort_by_favorite:
                Utility.setSortOrder(getActivity(), Utility.FAVORITE_MOVIES);
                items = null;
                movies = null;
                currentPage = PAGE_START;
                empty.setVisibility(View.VISIBLE);
                getActivity().getSupportLoaderManager().restartLoader(Utility.ID_FAVOURITE_LOADER, null, context);
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadAndUpdateSort(Context context, String sortBy, MenuItem item) {
        isLastPage = false;
        Utility.setSortOrder(context, sortBy);
        sortOrder = sortBy;
        items = null;
        movies = null;
        currentPage = PAGE_START;
        empty.setVisibility(View.VISIBLE);
        displayData(sortOrder);
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case Utility.ID_MOVIE_LOADER:
                return new AsyncTaskLoader<Cursor>(getActivity()) {
                    // Initialize a Cursor, this will hold all the movie data
                    Cursor mMovieData = null;

                    @Override
                    protected void onStartLoading() {
                        //super.onStartLoading();
                        if (mMovieData != null) {
                            // Delivers any previously loaded data immediately
                            deliverResult(mMovieData);
                        } else {
                            // Force a new load
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {
                        try {

//                            return getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                                    null,
//                                    null,
//                                    null, MovieEntry.COLUMN_RATINGS + " DESC");

                            // Filter results WHERE "title" = 'My Title'
                            String selection = MovieContract.MovieEntry.COLUMN_TITLE + " = ?";
                            String[] selectionArgs = { "Logan" };

                            return mDb.query(
                                    MovieContract.MovieEntry.TABLE_NAME,
                                    null,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                    MovieEntry.COLUMN_RATINGS + " DESC"
                            );



                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorView(new Throwable());
                            return null;
                        }
                    }

                    // deliverResult sends the result of the load, a Cursor, to the registered listener
                    public void deliverResult(Cursor data) {
                        mMovieData = data;
                        super.deliverResult(data);
                    }
                };
            case Utility.ID_FAVOURITE_LOADER:
                return new AsyncTaskLoader<Cursor>(getActivity()) {
                    // Initialize a Cursor, this will hold all the movie data

                    Cursor mFavoriteMovieData = null;

                    @Override
                    protected void onStartLoading() {
                        //super.onStartLoading();
                        if (mFavoriteMovieData != null) {
                            // Delivers any previously loaded data immediately
                            deliverResult(mFavoriteMovieData);
                        } else {
                            // Force a new load
                            forceLoad();
                        }
                    }

                    @Override
                    public Cursor loadInBackground() {


                        try {
                            return getActivity().getContentResolver().query(MovieEntry.FAVORITE_CONTENT_URI,
                                    null,
                                    null,
                                    null, MovieEntry.COLUMN_RELEASE_DATE);


                        } catch (Exception e) {
                            Log.e(TAG, "Failed to asynchronously load data.");
                            e.printStackTrace();
                            showErrorView(new Throwable());
                            return null;
                        }
                    }
                    // deliverResult sends the result of the load, a Cursor, to the registered listener
                    public void deliverResult(Cursor data) {
                        mFavoriteMovieData = data;
                        super.deliverResult(data);
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id == Utility.ID_MOVIE_LOADER){
            if(data.getCount()==0){
                showErrorView(new Throwable());
                return;
            }
        }
        Log.e(TAG, "Size of cursor is: " + data.getCount());
        movieList = new ArrayList<>();
        movieList.clear();
        if (data != null && data.moveToFirst()) {
            int movieIdIndex = data.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
            int movieTitleIndex = data.getColumnIndex(MovieEntry.COLUMN_TITLE);
            int moviePosterIndex = data.getColumnIndex(MovieEntry.COLUMN_POSTER);
            int movieRatingsIndex = data.getColumnIndex(MovieEntry.COLUMN_RATINGS);
            int movieReleaseDateIndex = data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
            int moviePlotIndex = data.getColumnIndex(MovieEntry.COLUMN_PLOT);
            do {
                int movieId = data.getInt(movieIdIndex);
                String movieTile = data.getString(movieTitleIndex);
                String moviePoster = data.getString(moviePosterIndex);
                Double movieRatings = data.getDouble(movieRatingsIndex);
                String movieReleaseDate = data.getString(movieReleaseDateIndex);
                String moviePlot = data.getString(moviePlotIndex);

                Results item = new Results();

                item.setId(movieId);
                item.setTitle(movieTile);
                item.setPosterPath(moviePoster);
                item.setVoteAverage(movieRatings);
                item.setReleaseDate(movieReleaseDate);
                item.setOverview(moviePlot);
                movieList.add(item);
            }
            while (data.moveToNext());

        }
        isLastPage = true;
        adapter = new MovieAdapter(movieList, Glide.with(getActivity()), getActivity(), this, this);
        grid.setAdapter(adapter);
        empty.setVisibility(View.GONE);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
