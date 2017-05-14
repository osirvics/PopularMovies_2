package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Victor on 5/13/2017.
 */

public class Trailers implements Parcelable {

    public Trailers(){

    }

    private int id;
    private ArrayList<Object> quicktime = null;
    private ArrayList<Youtube> youtube = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Object> getQuicktime() {
        return quicktime;
    }

    public void setQuicktime(ArrayList<Object> quicktime) {
        this.quicktime = quicktime;
    }

    public ArrayList<Youtube> getYoutube() {
        return youtube;
    }

    public void setYoutube(ArrayList<Youtube> youtube) {
        this.youtube = youtube;
    }


    protected Trailers(Parcel in) {
        id = in.readInt();
        if (in.readByte() == 0x01) {
            quicktime = new ArrayList<Object>();
            in.readList(quicktime, Object.class.getClassLoader());
        } else {
            quicktime = null;
        }
        if (in.readByte() == 0x01) {
            youtube = new ArrayList<Youtube>();
            in.readList(youtube, Youtube.class.getClassLoader());
        } else {
            youtube = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        if (quicktime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(quicktime);
        }
        if (youtube == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(youtube);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trailers> CREATOR = new Parcelable.Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel in) {
            return new Trailers(in);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}