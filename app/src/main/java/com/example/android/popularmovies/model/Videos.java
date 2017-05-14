package com.example.android.popularmovies.model;

/**
 * Created by Victor on 5/10/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Videos {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("results")
    @Expose
    private ArrayList<Video> results = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Video> getResults() {
        return results;
    }

    public void setResults(ArrayList<Video> results) {
        this.results = results;
    }

}