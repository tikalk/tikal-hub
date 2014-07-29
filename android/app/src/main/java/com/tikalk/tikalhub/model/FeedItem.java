package com.tikalk.tikalhub.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FeedItem {
    private Date date;
    private long id;
    private String message;

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

    static final SimpleDateFormat fbDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static FeedItem createFromFacebookJsonResponce(JSONObject jsonItem) throws JSONException, ParseException {

        Date date = fbDateFormatter.parse(jsonItem.getString("created_time"));

        long id = date.getTime(); // TODO resolve unique id

        FeedItem feedItem = new FeedItem(id, date);
        String message = jsonItem.optString("message");
        if(message == null || message.isEmpty())
            message = jsonItem.optString("story");
        feedItem.setMessage(message);
        return  feedItem;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
