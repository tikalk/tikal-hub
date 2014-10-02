package com.tikalk.tikalhub;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initDrawer();

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

    private void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        final ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        final ArrayAdapter<NavigationMenuItem> adapter = new ArrayAdapter<NavigationMenuItem>(this,
                R.layout.drawer_list_item, new NavigationMenuItem[]{
                new NavigationMenuItem(getString(R.string.menu_news_feed_id), getString(R.string.menu_news_feed)),
                new NavigationMenuItem(getString(R.string.menu_settings_id), getString(R.string.menu_settings))
        });
        drawerList.setAdapter(adapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NavigationMenuItem item = adapter.getItem(position);
                if (onDrawerItemSelected(item)) {
                    // Highlight the selected item, update the title, and close the drawer
                    drawerList.setItemChecked(position, true);
                }

                drawerLayout.closeDrawer(drawerList);
            }
        });

    }

    class NavigationMenuItem {
        private String id;
        private String title;

        NavigationMenuItem(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return getTitle();
        }
    }

    private boolean onDrawerItemSelected(NavigationMenuItem item) {

        if (item.getId() == getString(R.string.menu_settings_id)) {
            startActivity(new Intent(this, SettingsActivity.class));
            return false;
        }

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(Gravity.START);

        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
