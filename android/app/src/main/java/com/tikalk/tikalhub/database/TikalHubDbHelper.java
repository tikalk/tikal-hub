package com.tikalk.tikalhub.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tikalk.tikalhub.database.TikalHubDbContract.FeedEntry;
import com.tikalk.tikalhub.database.TikalHubDbContract.Contacts;
import com.tikalk.tikalhub.model.FeedRawItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TikalHubDbHelper extends SQLiteOpenHelper {

    public static String LogTag = "TikalHubDbHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TikalHub.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_SOURCE_TYPE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_SOURCE_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_CREATED_TIME + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_UPDATED_TIME + INTEGER_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_ENTRY_RAW_DATA + TEXT_TYPE +
            " );";
    private static final String SQL_CREATE_ENTRIES_ITEM_KEY = "CREATE UNIQUE INDEX ItemKey ON " + FeedEntry.TABLE_NAME + "(" +
                    FeedEntry.COLUMN_NAME_SOURCE_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_SOURCE_ID + COMMA_SEP +
                    FeedEntry.COLUMN_ENTRY_ID +
            ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE " + Contacts.TABLE_NAME + " (" +
                    Contacts._ID + " INTEGER PRIMARY KEY," +
                    Contacts.COLUMN_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    Contacts.COLUMN_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                    Contacts.COLUMN_RAW_DATA + TEXT_TYPE +
                    " );";

    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + Contacts.TABLE_NAME;

    public TikalHubDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_ITEM_KEY);
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACTS);

        sqLiteDatabase.execSQL("INSERT INTO  " + Contacts.TABLE_NAME + " (" +
                Contacts.COLUMN_FIRST_NAME + COMMA_SEP +
                Contacts.COLUMN_LAST_NAME +
                " ) VALUES ('Igor', 'Zelmanovich')");
        sqLiteDatabase.execSQL("INSERT INTO  " + Contacts.TABLE_NAME + " (" +
                Contacts.COLUMN_FIRST_NAME + COMMA_SEP +
                Contacts.COLUMN_LAST_NAME +
                " ) VALUES ('Joe', 'Smith')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_CONTACTS);

        onCreate(sqLiteDatabase);
    }

    public synchronized void saveFeedItems(List<FeedRawItem> items) {

        SQLiteDatabase db = getWritableDatabase();

        for(FeedRawItem item: items) {

            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_NAME_SOURCE_TYPE, item.getSourceType());
            values.put(FeedEntry.COLUMN_NAME_SOURCE_ID, item.getSourceId());
            values.put(FeedEntry.COLUMN_ENTRY_ID, item.getEntryId());
            values.put(FeedEntry.COLUMN_CREATED_TIME, item.getCreatedTime().getTime());
            values.put(FeedEntry.COLUMN_UPDATED_TIME, item.getUpdatedTime().getTime());
            values.put(FeedEntry.COLUMN_ENTRY_RAW_DATA, item.getRawData());

            try {
                db.replaceOrThrow(FeedEntry.TABLE_NAME, null, values);
            } catch (SQLException e) {
                Log.e(LogTag, "Failed insert/update feed entry", e);
            }
        }
    }

    public List<FeedRawItem> getFeedItems() {

        ArrayList rawItems = new ArrayList<FeedRawItem>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(FeedEntry.TABLE_NAME, new String[]{
                FeedEntry.COLUMN_NAME_SOURCE_TYPE,
                FeedEntry.COLUMN_NAME_SOURCE_ID,
                FeedEntry.COLUMN_ENTRY_ID,
                FeedEntry.COLUMN_CREATED_TIME,
                FeedEntry.COLUMN_UPDATED_TIME,
                FeedEntry.COLUMN_ENTRY_RAW_DATA
        }, null, null, null, null, FeedEntry.COLUMN_CREATED_TIME + " DESC", "100");


        if(c != null && c.moveToFirst()) {

            do{
                String sourceType = c.getString(c.getColumnIndex(FeedEntry.COLUMN_NAME_SOURCE_TYPE));
                String sourceId = c.getString(c.getColumnIndex(FeedEntry.COLUMN_NAME_SOURCE_ID));
                String entryId = c.getString(c.getColumnIndex(FeedEntry.COLUMN_ENTRY_ID));
                Date createdTime =new Date( c.getLong(c.getColumnIndex(FeedEntry.COLUMN_CREATED_TIME)));
                Date updatedTime =new Date( c.getLong(c.getColumnIndex(FeedEntry.COLUMN_UPDATED_TIME)));
                String rawData = c.getString(c.getColumnIndex(FeedEntry.COLUMN_ENTRY_RAW_DATA));

                rawItems.add(new FeedRawItem(sourceType, sourceId, entryId, createdTime, updatedTime, rawData));

            }while (c.moveToNext());
        }

        return rawItems;
    }
}
