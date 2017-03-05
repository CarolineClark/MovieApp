package com.scribbleheart.movieapp.utils;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

// should this be moved into the MovieBean?
public class ReviewBean {
    private String author;
    private String content;

    public ReviewBean(JSONObject reviewJson) {
        try {
            author = reviewJson.getString("author");
            content = reviewJson.getString("content");
        } catch (JSONException e) {
            // do nothing
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
