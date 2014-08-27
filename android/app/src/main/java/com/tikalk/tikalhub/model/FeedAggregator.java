package com.tikalk.tikalhub.model;

import android.content.Context;

import com.tikalk.tikalhub.database.TikalHubDbHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FeedAggregator {

    private ArrayList<FeedItem> cachedItems;
    private final List<FeedSource> sources = new ArrayList<FeedSource>();

    private static FeedAggregator instance;
    private TikalHubDbHelper dbHelper;

    public static void init(Context context) {
        instance = new FeedAggregator(context);
    }

    public static FeedAggregator getInstance() {
        return instance;
    }

    private FeedAggregator(Context context) {
        dbHelper = new TikalHubDbHelper(context);
        sources.add(new FacebookFeedSource(context, "225585444263260/feed")); //full stack developers
        sources.add(new FacebookFeedSource(context, "146340308712396/feed")); //tikal knowledge
    }

    public List<FeedItem> getItems(boolean refresh) {

        if(refresh) {
            updateFeeds();
        }

        List<FeedRawItem> rawItems = dbHelper.getFeedItems();
        ArrayList items = new ArrayList<FeedItem>();

        for(FeedRawItem rawItem: rawItems) {

            try {
                FeedItem item = FacebookFeedSource.createFromRawItem(rawItem);
                items.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return items;

    }

    public void updateFeeds() {

        for(FeedSource source: sources) {
            List<FeedRawItem> items = source.fetchItems();
            dbHelper.saveFeedItems(items);
        }

    }
}
