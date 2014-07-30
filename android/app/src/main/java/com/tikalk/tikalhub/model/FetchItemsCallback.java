package com.tikalk.tikalhub.model;

import java.util.List;

public interface FetchItemsCallback {
    void onItemsLoaded(List<FeedItem> feedItems);
}
