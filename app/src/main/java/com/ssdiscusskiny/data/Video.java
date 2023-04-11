package com.ssdiscusskiny.data;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Video {
    private String id;
    public Video(String id) {
        this.id = id;

    }

    public String getId() {
        return id;
    }


}
