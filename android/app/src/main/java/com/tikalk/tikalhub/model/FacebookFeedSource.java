package com.tikalk.tikalhub.model;

import android.content.Context;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacebookFeedSource implements FeedSource {

    private Context context;
    private String graphPath;

    public FacebookFeedSource(Context context, String graphPath) {
        this.context = context;

        this.graphPath = graphPath;
    }

    @Override
    public void fetchItems(final FetchItemsCallback callback) {

        Session session = Session.getActiveSession();
        if(session == null)
            session = Session.openActiveSessionFromCache(context);

        if (session != null && session.isOpened()) {
            // load data from FB
            Request.newGraphPathRequest(session, graphPath, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {

                    if(response.getError() != null) {
                        return;
                    }

                    List<FeedItem> feedItems = new ArrayList<FeedItem>();
                    GraphObject object = response.getGraphObject();
                    try {

                        JSONArray jsonList = (JSONArray) object.getProperty("data");

                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject jsonItem = (JSONObject) jsonList.get(i);
                            FeedItem feedItem = FeedItem.createFromFacebookJsonResponse(jsonItem);
                            feedItems.add(feedItem);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    callback.onItemsLoaded(feedItems);
                }
            }).executeAsync();
        }

    }

}
