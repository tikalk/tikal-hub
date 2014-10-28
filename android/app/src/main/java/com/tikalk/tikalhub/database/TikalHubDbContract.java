package com.tikalk.tikalhub.database;

import android.provider.BaseColumns;

public final class TikalHubDbContract {

    public static abstract class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "feed_entry";
        public static final String COLUMN_NAME_SOURCE_TYPE = "source_type";
        public static final String COLUMN_NAME_SOURCE_ID = "source_id";
        public static final String COLUMN_ENTRY_ID = "entry_id";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_UPDATED_TIME = "updated_time";
        public static final String COLUMN_ENTRY_RAW_DATA = "raw_data";

    }

    public static abstract class Contacts implements BaseColumns {

        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_RAW_DATA = "raw_data";
    }
}
