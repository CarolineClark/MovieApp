package com.scribbleheart.movieapp.utils;

import org.json.JSONException;
import org.json.JSONObject;

// should this be moved into the MovieBean?
public class ReviewBean {
    private String author;
    private String content;

    public ReviewBean(JSONObject jsonObj) {
        try {
            author = jsonObj.getString("author");
            content = jsonObj.getString("content");
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
