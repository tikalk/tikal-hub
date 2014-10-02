package com.tikalk.tikalhub.ui;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tikalk.tikalhub.R;
import com.tikalk.tikalhub.model.FeedAggregator;
import com.tikalk.tikalhub.model.FeedItem;

public class NewsFeedFragment extends Fragment {

    private ListView listView;
    private UpdatesListAdapter listAdapter;
    private ProgressBar progressBar;
    private FeedAggregator feedAggregator;
    private FeedAggregator.Listener feedAggregatorListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_news_feed, container, false);


        feedAggregator = FeedAggregator.getInstance();

        listAdapter = new UpdatesListAdapter(this, inflater, feedAggregator);

        listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsFeedFragment.this.onItemClick(view, (FeedItem) listAdapter.getItem(i));
            }
        });

        // Create a progress bar to display while the list loads
        View emptyView = inflater.inflate(R.layout.updates_empty, null);
        Button button = (Button)emptyView.findViewById(R.id.button_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });
        listView.setEmptyView(emptyView);

        // Must add the progress bar to the root of the layout
        rootView.addView(emptyView);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);


        listAdapter.load(false);

        feedAggregatorListener = new FeedAggregator.Listener() {
            @Override
            public void onUpdated() {
                listAdapter.load(false);
            }
        };

        feedAggregator.addListener(feedAggregatorListener);


        return rootView;
    }


    @Override
    public void onDestroy() {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
