package com.tikalk.tikalhub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.tikalk.tikalhub.model.FeedItem;
import com.tikalk.tikalhub.ui.UpdatesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ListView listView;
    private UpdatesListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new UpdatesListAdapter(this);

        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);

        Session session = Session.openActiveSession(this, false, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {

        if (session.isOpened()) {
            // load data from FB
            Request.newGraphPathRequest(session, "TikalKnowledge/feed", new Request.Callback() {
                @Override
                public void onCompleted(Response response) {

                    List<FeedItem> feedItems = new ArrayList<FeedItem>();
                    GraphObject object = response.getGraphObject();
                    try {

                        JSONArray jsonList = (JSONArray) object.getProperty("data");

                        for (int i = 0; i < jsonList.length(); i++) {
                            JSONObject jsonItem = (JSONObject) jsonList.get(i);
                            FeedItem feedItem = FeedItem.createFromFacebookJsonResponce(jsonItem);
                            feedItems.add(feedItem);
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                    }

                    listAdapter.addItems(feedItems);
                }
            }).executeAsync();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
