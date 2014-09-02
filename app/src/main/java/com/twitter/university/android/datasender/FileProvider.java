package com.twitter.university.android.datasender;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.Map;


public class FileProvider extends ContentProvider {
    private static final int EXTRA_DIR_TYPE = 1;
    private static final int FILES_DIR_TYPE = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.Extras.TABLE,
            EXTRA_DIR_TYPE);
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.Files.TABLE,
            FILES_DIR_TYPE);
    }

    private static final Map<String, String> PROJ_MAP_EXTRAS = new ProjectionMap.Builder()
        .addColumn(SenderContract.Extras.Columns.ID, DbHelper.COL_ID)
        .addColumn(SenderContract.Extras.Columns.EXTRA, DbHelper.COL_EXTRA)
        .addColumn(DbHelper.COL_EXTRA, DbHelper.COL_EXTRA)
        .build()
        .getProjectionMap();


    private DbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.d("CP", "Type " + uri);
        switch (MATCHER.match(uri)) {
            case EXTRA_DIR_TYPE:
                return SenderContract.Extras.MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        StringBuilder buf = new StringBuilder();
        for (String s: proj) { buf.append(" ").append(s); }
        Log.d("CP", "Query@ " + uri + " :" + buf.toString());

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        long pk = -1;
        switch (MATCHER.match(uri)) {
            case EXTRA_DIR_TYPE:
                qb.setTables(DbHelper.TABLE_EXTRA);
                qb.setProjectionMap(PROJ_MAP_EXTRAS);
                break;
            default:
                throw new IllegalArgumentException("Unexpected uri: " + uri);
        }

        if (0 < pk) { qb.appendWhere(DbHelper.COL_ID + "=" + pk); }

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d("CP", "Query: " + c.getPosition());
        while (c.moveToNext()) {
            for (int i = 0; i < c.getColumnCount(); i++) {
                Log.d("CP", "extra @ " + i + ": " + c.getString(i));
            }
        }
        c.moveToPosition(-1);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("update not supported");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
        throws FileNotFoundException
    {
        Log.d("CP", "Open file @" + mode + " : " + uri);

        return null;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        Log.d("CP", "openAssetFile @" + mode + " : " + uri);
        return super.openAssetFile(uri, mode);
    }

    @Override
    public String[] getStreamTypes(Uri uri, String filter) {
        Log.d("CP", "getStreamTypes @ " + filter + " : " + uri);
        return super.getStreamTypes(uri, filter);
    }

    @Override
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String filter, Bundle opts) throws FileNotFoundException {
        Log.d("CP", "openTypedAssetFile @ " + filter + " : " + uri);
        return super.openTypedAssetFile(uri, filter, opts);
    }

    @Override
    public <T> ParcelFileDescriptor openPipeHelper(Uri uri, String mimeType, Bundle opts, T args, PipeDataWriter<T> func)
        throws FileNotFoundException
    {
        Log.d("CP", "openPipeHelper @ " + mimeType + " : " + uri);
        return super.openPipeHelper(uri, mimeType, opts, args, func);
    }

    private SQLiteDatabase getDb() { return helper.getWritableDatabase(); }
}
