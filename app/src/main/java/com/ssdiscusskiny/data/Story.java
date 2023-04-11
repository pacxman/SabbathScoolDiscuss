package com.ssdiscusskiny.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class Story implements Parcelable {

    private final String keys;

    private final String title;
    private final String imageUrl;

    public Story(String keys, String title, @Nullable String imageUrl) {
        this.title = title;
        if (imageUrl!=null) this.imageUrl = imageUrl;
        else this.imageUrl = "no_url";

        this.keys = keys;

    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getKeys() {
        return keys;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.title, this.imageUrl, Arrays.asList(keys).toString()});
    }
}
