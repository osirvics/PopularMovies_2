package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.android.popularmovies.model.Movies;
import com.example.android.popularmovies.model.Results;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmovies.api.TheMoviedbClient.getClient;


public class MainActivityFragment extends Fragment implements PaginationAdapterCallback, OnItemClickListener {
    private RecyclerView grid;
    private ProgressBar empty;
    private MovieAdapter adapter;
    private LinearLayout errorLayout;
    private ArrayList<Results> movies;
    private Movies items;
    private Button btnRetry;
    private TextView txtError;
    int pageLoadCount = 0;
    private static final int PAGE_START = 1;
    //Indicates if footer ProgressBar is shown
    private boolean isLoading = false;
    private boolean isLastPage = false;
    //setting initial value to ten
    private int TOTAL_PAGES = 10;
    private int currentPage = PAGE_START;
    public static final int PER_PAGE = 20;
    //Default movie sort order
    private String sortOrder = Utility.DEESCENDING_POPULARITY;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        setupView(view);
        btnRetry.setOnClickListener(retry);
        sortOrder = Utility.getSortOrder(getActivity());
        displayData(sortOrder);
        return  view;
    }
    View.OnClickListener retry = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideErrorView();
            displayData(sortOrder);
        }
    });


    private void populateGrid() {
        adapter = new MovieAdapter(movies, Glide.with(getActivity()) , getActivity(), this, this);
        grid.setAdapter(adapter);
        empty.setVisibility(View.GONE);
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
    }

    public void setupView(View view){
        grid = (RecyclerView) view.findViewById(R.id.image_grid);
        empty = (ProgressBar) view.findViewById(R.id.empty);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        btnRetry = (Button) view.findViewById(R.id.error_btn_retry);
        txtError = (TextView) view.findViewById(R.id.error_txt_cause);
        final GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(adapter.getItemViewType(position)){
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

    private void displayData(String sortBy) {
        if (items != null) {
            populateGrid();
        } else {
            TheMoviedbApiService apiService =
                    getClient().create(TheMoviedbApiService.class);
            Call<Movies> call;
            if(sortBy.contains(Utility.DEESCENDING_POPULARITY)){
                call = apiService.getPopularMovies(BuildConfig.MOVIE_API_KEY, currentPage);
            }
            else{
                call = apiService.getTopRatedMovies(BuildConfig.MOVIE_API_KEY, currentPage);
            }
            call.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    if(response.code()== 200){
                        items =  new Movies();
                        items = response.body();
                        movies =  new ArrayList<>();
                        movies= items.getResults();
                        populateGrid();
                        int totalCount = items.getTotalPages();
                        if(totalCount % PER_PAGE ==0)
                            TOTAL_PAGES = (totalCount/PER_PAGE);
                        else TOTAL_PAGES = (totalCount/PER_PAGE) + 1;
                    }
                }
                @Override
                public void onFailure(Call<Movies> call, Throwable t) {
                    t.printStackTrace();
                    showErrorView(t);
                }
            });
        }
    }

    private void loadNextPage(String sortBy) {
        pageLoadCount +=1;
        TheMoviedbApiService apiService =
                TheMoviedbClient.getClient().create(TheMoviedbApiService.class);
        Call<Movies> call;
        if(sortBy.contains(Utility.DEESCENDING_POPULARITY)){
            call = apiService.getPopularMovies(BuildConfig.MOVIE_API_KEY, currentPage);
        }
        else{
            call = apiService.getTopRatedMovies(BuildConfig.MOVIE_API_KEY, currentPage);
        }
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {

                if(response.code()==200){
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    final Movies items = response.body();
                    final ArrayList<Results> results = items.getResults();
                    adapter.addAll(results);
                    adapter.notifyDataSetChanged();
                    if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                    else isLastPage = true;
                }
            }
            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
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
    public void  OnItemClick(int position) {
        Intent intent = new Intent(getActivity() , DetailActivity.class);
        intent.putExtra("data", movies.get(position));
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
        if( sortOrder.contains(Utility.DEESCENDING_POPULARITY)){
            item = menu.findItem(R.id.menu_sort_by_asc);
            item.setChecked(true);
        }
        else if(sortOrder.contains(Utility.VOTE_AVERAGE)){
            item = menu.findItem( R.id.menu_sort_by_vote_average);
            item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_asc:
               reloadAndUpdateSort(getActivity(), Utility.DEESCENDING_POPULARITY);
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            case R.id.menu_sort_by_vote_average:
                reloadAndUpdateSort(getActivity(),Utility.VOTE_AVERAGE);
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadAndUpdateSort(Context context, String sortBy){
        Utility.setSortOrder(context, sortBy);
        sortOrder = sortBy;
        items = null;
        movies = null;
        currentPage = PAGE_START;
        empty.setVisibility(View.VISIBLE);
        displayData(sortOrder);
    }


}
