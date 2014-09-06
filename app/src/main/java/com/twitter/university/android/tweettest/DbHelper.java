package com.twitter.university.android.tweettest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    public static final String DB_FILE = "image_refs.db";
    public static final int VERSION = 3;

    static final String TABLE_EXTRA = "image_refs";

    public DbHelper(Context context) {
        super(context, DB_FILE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_EXTRA + "("
                + BaseColumns._ID + " integer PRIMARY KEY, "
                + TwitterContract.Columns.DATA + " string)");

        ContentValues row = new ContentValues();
        row.put(BaseColumns._ID, App.ID_BAD_REF);
        row.put(TwitterContract.Columns.DATA, App.BAD_FILE);
        db.insert(TABLE_EXTRA, null, row);

        row.clear();
        row.put(BaseColumns._ID, App.ID_GOOD_REF);
        row.put(TwitterContract.Columns.DATA, App.GOOD_FILE);
        db.insert(TABLE_EXTRA, null, row);

        Log.d(TAG, "DB created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int from, int to) {
        throw new UnsupportedOperationException("upgrade not supported");
    }
}
