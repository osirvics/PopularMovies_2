package com.example.android.popularmovies.model;

import java.util.ArrayList;

/**
 * Created by Victor on 5/10/2017.
 */





public class Reviews {

    private int id;
    private int page;
    private ArrayList<Review> results = null;
    private int totalPages;
    private int totalResults;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<Review> getResults() {
        return results;
    }

    public void setResults(ArrayList<Review> results) {
        this.results = results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
