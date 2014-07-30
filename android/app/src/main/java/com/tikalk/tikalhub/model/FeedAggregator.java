package com.tikalk.tikalhub.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class FeedAggregator {

    private ArrayList<FeedItem> cachedItems;
    private final List<FeedSource> sources = new ArrayList<FeedSource>();

    private static FeedAggregator instance;

    public static void init(Context context) {
        instance = new FeedAggregator(context);
    }

    public static FeedAggregator getInstance() {
        return instance;
    }

    private FeedAggregator(Context context) {
        sources.add(new FacebookFeedSource(context, "225585444263260/feed")); //full stack developers
        sources.add(new FacebookFeedSource(context, "146340308712396/feed")); //tikal knowledge
    }

    public void getItems(final FetchItemsCallback callback, boolean refresh) {

        if(cachedItems == null || refresh) {
            final ArrayList items = new ArrayList<FeedItem>();

            for(FeedSource source: sources) {
                source.fetchItems(new FetchItemsCallback() {
                    @Override
                    public void onItemsLoaded(List<FeedItem> feedItems) {
                        items.addAll(feedItems);
                        callback.onItemsLoaded(feedItems);
                    }
                });
            }

            this.cachedItems = items;

        } else {
            callback.onItemsLoaded(cachedItems);
        }
    }
}
