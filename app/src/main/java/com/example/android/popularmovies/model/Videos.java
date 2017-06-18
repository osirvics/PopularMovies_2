package com.example.android.popularmovies.model;

/**
 * Created by Victor on 5/10/2017.
 */

import java.util.ArrayList;


public class Videos {


    private int id;

    private ArrayList<Video> youtube = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Video> getYoutube() {
        return youtube;
    }

    public void setYoutube(ArrayList<Video> youtube) {
        this.youtube = youtube;
    }

}