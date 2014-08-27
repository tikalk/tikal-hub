package com.tikalk.tikalhub.model;

import android.content.Context;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FacebookFeedSource implements FeedSource {

    private Context context;
    private String graphPath;

    static final SimpleDateFormat fbDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public FacebookFeedSource(Context context, String graphPath) {
        this.context = context;

        this.graphPath = graphPath;
    }

    @Override
    public String getSourceType() {
        return "facebook";
    }

    @Override
    public String getSourceId() {
        return graphPath;
    }

    @Override
    public List<FeedRawItem> fetchItems() {

        Session session = Session.getActiveSession();
        if(session == null)
            session = Session.openActiveSessionFromCache(context);

        final List<FeedRawItem> feedItems = new ArrayList<FeedRawItem>();

        if (session != null && session.isOpened()) {
            // load data from FB
            Request.newGraphPathRequest(session, graphPath, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {

                    if(response.getError() != null) {
                        return;
                    }

                    GraphObject object = response.getGraphObject();
                    try {

                        JSONArray jsonList = (JSONArray) object.getProperty("data");

                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject jsonItem = (JSONObject) jsonList.get(i);
                            String rawData = jsonItem.toString();
                            String entryId = jsonItem.getString("id");
                            Date createdTime = fbDateFormatter.parse(jsonItem.getString("created_time"));
                            Date updatedTime = fbDateFormatter.parse(jsonItem.getString("updated_time"));
                            feedItems.add(new FeedRawItem(getSourceType(), getSourceId(), entryId, createdTime, updatedTime, rawData));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }).executeAndWait();
        }

        return feedItems;
    }

    public static FeedItem createFromRawItem(FeedRawItem rawItem) throws JSONException {

        JSONObject jsonItem = new JSONObject(rawItem.getRawData());
        Date date = rawItem.getCreatedTime();

        long id = date.getTime() | (1L * (rawItem.getSourceType().hashCode() ^ rawItem.getSourceId().hashCode() ^ rawItem.getEntryId().hashCode()) << 32);

        FeedItem feedItem = new FeedItem(id, date);
        String message = jsonItem.optString("message");
        if(message == null || message.isEmpty())
            message = jsonItem.optString("story");
        feedItem.setMessage(message);
        feedItem.setLink(jsonItem.optString("link"));
        feedItem.setImageUrl(jsonItem.optString("picture"));

        return  feedItem;
    }

}
