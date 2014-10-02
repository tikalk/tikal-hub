package com.tikalk.tikalhub.model;


import java.util.Date;

public class FeedRawItem {

    private String sourceType;
    private String sourceId;
    private String entryId;
    private Date createdTime;
    private Date updatedTime;
    private String rawData;

    public FeedRawItem(String sourceType, String sourceId, String entryId, Date createdTime, Date updatedTime, String rawData) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.entryId = entryId;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.rawData = rawData;
    }

    public String getRawData() {
        return rawData;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

}
