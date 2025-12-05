package com.example.lingonary_.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Podcast implements Parcelable {
    private String title;
    private String description;

    public Podcast(String title, String description) {
        this.title = title;
        this.description = description;
    }

    protected Podcast(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<Podcast> CREATOR = new Creator<Podcast>() {
        @Override
        public Podcast createFromParcel(Parcel in) {
            return new Podcast(in);
        }

        @Override
        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }
}
