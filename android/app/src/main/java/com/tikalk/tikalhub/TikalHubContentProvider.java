package com.tikalk.tikalhub;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.tikalk.tikalhub.database.TikalHubDbContract.Contacts;
import com.tikalk.tikalhub.database.TikalHubDbHelper;


public class TikalHubContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.tikalk.tikalhub.provider";
    private static final int CONTACTS = 0;
    private static final int CONTACTS_ID = 1;

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_CONTACTS_URI = Uri.withAppendedPath(AUTHORITY_URI, Contacts.TABLE_NAME);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private TikalHubDbHelper dbHelper;

    static {

        uriMatcher.addURI(AUTHORITY, Contacts.TABLE_NAME, CONTACTS);
        uriMatcher.addURI(AUTHORITY, Contacts.TABLE_NAME + "/#", CONTACTS_ID);

    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match)
        {
            case CONTACTS:
                return "vnd.android.cursor.dir/tikal-contact";
            case CONTACTS_ID:
                return "vnd.android.cursor.item/tikal-contact";
            default:
                return null;
        }
    }

    @Override
    public boolean onCreate() {

        dbHelper = new TikalHubDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int match = uriMatcher.match(uri);

        switch (match) {
            case CONTACTS:
                return queryContacts(projection, selection, selectionArgs, sortOrder);
            case CONTACTS_ID:
                // TODO
                break;
        }
        return null;
    }

    private Cursor queryContacts(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(Contacts.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
