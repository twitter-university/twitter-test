package com.twitter.university.android.datasender;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_FILE = "file";
    public static final int VERSION = 1;

    static final String TABLE_EXTRA = "file";
    static final String COL_ID = BaseColumns._ID;
    static final String COL_EXTRA = "_data";

    public DbHelper(Context context) {
        super(context, DB_FILE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_EXTRA + "("
                + COL_ID + " integer PRIMARY KEY AUTOINCREMENT, "
                + COL_EXTRA + " string)");

        ContentValues row = new ContentValues();
        row.put(COL_EXTRA, SenderContract.Files.URI.buildUpon().appendPath("nubbly").build().toString());
        db.insert(TABLE_EXTRA, null, row);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int from, int to) {
        throw new UnsupportedOperationException("upgrade not supported");
    }
}
