package com.tikalk.tikalhub.model;

import android.graphics.Point;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedItem {
    private Date date;
    private long id;
    private String message;
    private String link;
    private String imageUrl;
    private Point imageSize;

    public FeedItem(long id, Date date) {
        this.id = id;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageSize(Point imageSize) {
        this.imageSize = imageSize;
    }

    public Point getImageSize() {
        return imageSize;
    }
}
