package com.tikalk.tikalhub.model;

public interface FeedSource {
    void fetchItems(FetchItemsCallback callback);
}
