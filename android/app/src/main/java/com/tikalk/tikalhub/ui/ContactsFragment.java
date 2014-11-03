package com.tikalk.tikalhub.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.tikalk.tikalhub.R;
import com.tikalk.tikalhub.TikalHubContentProvider;
import com.tikalk.tikalhub.database.TikalHubDbContract.Contacts;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter listAdapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);

        listAdapter = new SimpleCursorAdapter(getActivity(), R.layout.contacts_list_item, null, new String [] {Contacts.COLUMN_FIRST_NAME, Contacts.COLUMN_LAST_NAME},
                new int[] {R.id.first_name, R.id.last_name}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(listAdapter);

        return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TikalHubContentProvider.CONTENT_CONTACTS_URI,
                new String[] {Contacts._ID, Contacts.COLUMN_FIRST_NAME, Contacts.COLUMN_LAST_NAME}, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);

    }
}
