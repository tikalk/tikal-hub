package com.tikalk.tikalhub;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tikalk.tikalhub.ui.ContactsFragment;
import com.tikalk.tikalhub.ui.NewsFeedFragment;


public class MainActivity extends Activity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentTabId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        int tabId = R.string.menu_news_feed;

        if(savedInstanceState != null) {
            currentTabId = savedInstanceState.getInt("selectedTab", tabId);
        }

        showContent(tabId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selectedTab", currentTabId);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showContent(int tabId) {

        if(currentTabId == tabId)
            return;

        Fragment fragment;
        switch (tabId){
            case R.string.menu_news_feed:
                fragment = new NewsFeedFragment();
                break;
            case R.string.menu_contacts:
                fragment= new ContactsFragment();
                break;
            default:
                return;
        }

        currentTabId = tabId;
        getFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        // TODO

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
                new NavigationMenuItem(R.string.menu_news_feed, getString(R.string.menu_news_feed)),
                new NavigationMenuItem(R.string.menu_contacts, getString(R.string.menu_contacts)),
                new NavigationMenuItem(R.string.menu_settings, getString(R.string.menu_settings))
        });
        drawerList.setAdapter(adapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NavigationMenuItem item = adapter.getItem(position);
                onDrawerItemSelected(item);
                // prevent selection by clicking
                drawerList.setItemChecked(position, false);

                drawerLayout.closeDrawer(drawerList);
            }
        });

    }

    class NavigationMenuItem {
        private int id;
        private String title;

        NavigationMenuItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
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

    private void onDrawerItemSelected(NavigationMenuItem item) {

        if (item.getId() == R.string.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }

        showContent(item.getId());
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
        return super.onOptionsItemSelected(item);
    }
}
