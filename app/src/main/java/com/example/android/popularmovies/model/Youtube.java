package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Victor on 5/13/2017.
 */

public class Youtube implements Parcelable {

    private String name;
    private String size;
    private String source;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    protected Youtube(Parcel in) {
        name = in.readString();
        size = in.readString();
        source = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(size);
        dest.writeString(source);
        dest.writeString(type);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Youtube> CREATOR = new Parcelable.Creator<Youtube>() {
        @Override
        public Youtube createFromParcel(Parcel in) {
            return new Youtube(in);
        }

        @Override
        public Youtube[] newArray(int size) {
            return new Youtube[size];
        }
    };
}