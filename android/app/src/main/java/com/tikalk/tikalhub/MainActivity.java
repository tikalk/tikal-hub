package com.tikalk.tikalhub;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tikalk.tikalhub.model.FeedAggregator;
import com.tikalk.tikalhub.model.FeedItem;
import com.tikalk.tikalhub.ui.UpdatesListAdapter;


public class MainActivity extends Activity {

    private ListView listView;
    private UpdatesListAdapter listAdapter;
    private ProgressBar progressBar;
    private FeedAggregator feedAggregator;
    private FeedAggregator.Listener feedAggregatorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedAggregator = FeedAggregator.getInstance();

        listAdapter = new UpdatesListAdapter(this, feedAggregator);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.onItemClick(view, (FeedItem) listAdapter.getItem(i));
            }
        });

        // Create a progress bar to display while the list loads
        View emptyView = getLayoutInflater().inflate(R.layout.updates_empty, null);
        Button button = (Button)emptyView.findViewById(R.id.button_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });
        listView.setEmptyView(emptyView);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(emptyView);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);


        listAdapter.load(false);

        feedAggregatorListener = new FeedAggregator.Listener() {
            @Override
            public void onUpdated() {
                listAdapter.load(false);
            }
        };

        feedAggregator.addListener(feedAggregatorListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        feedAggregator.removeListener(feedAggregatorListener);
    }

    private void refreshList() {
        listAdapter.load(true);
    }

    public void setLoading(boolean loading) {
        if(loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void onItemClick(View view, FeedItem feedItem) {
        String link = feedItem.getLink();
        if(link != null && !link.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_refresh:
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
