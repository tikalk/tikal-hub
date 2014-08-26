package com.tikalk.tikalhub.model;

import java.util.List;

public interface FeedSource {
    List<FeedRawItem> fetchItems();

    String getSourceType();

    String getSourceId();
}
