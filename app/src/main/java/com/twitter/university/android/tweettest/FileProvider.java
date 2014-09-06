package com.twitter.university.android.tweettest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileProvider extends ContentProvider {
    private static final String TAG = "CP";

    private static final int TYPED_ASSETS_DIR_TYPE = 1;
    private static final int ASSETS_DIR_TYPE = 2;
    private static final int FILES_DIR_TYPE = 3;
    private static final int EXTRA_ITEM_TYPE = 4;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.TypedAssets.TABLE,
            TYPED_ASSETS_DIR_TYPE);
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.Assets.TABLE,
            ASSETS_DIR_TYPE);
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.Files.TABLE,
            FILES_DIR_TYPE);
        MATCHER.addURI(
            SenderContract.AUTHORITY,
            SenderContract.Extras.TABLE + "/#",
            EXTRA_ITEM_TYPE);
    }


    private DbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "Type " + uri);
        switch (MATCHER.match(uri)) {
            case TYPED_ASSETS_DIR_TYPE:
            case ASSETS_DIR_TYPE:
            case FILES_DIR_TYPE:
            case EXTRA_ITEM_TYPE:
                return SenderContract.MIME_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        StringBuilder buf = new StringBuilder();
        for (String s: proj) { buf.append(" ").append(s); }
        Log.d(TAG, "query@ " + uri + " :" + buf.toString());

        switch (MATCHER.match(uri)) {
            case EXTRA_ITEM_TYPE:
                break;
            default:
                return null;  // Important!  Don't die if you don't recognize it!
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DbHelper.TABLE_EXTRA);
        qb.appendWhere(BaseColumns._ID + "=" + uri.getLastPathSegment());
        return qb.query(getDb(), proj, sel, selArgs, null, null, sort);
    }

    @Override
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String filter, Bundle opts)
        throws FileNotFoundException
    {
        String fname = uri.getQueryParameter(SenderContract.PARAM_FNAME);
        Log.d(TAG, "openTypedAssetFile: " + fname + "@" + filter + " # " + opts + "  <= " + uri);
        switch (MATCHER.match(uri)) {
            case TYPED_ASSETS_DIR_TYPE:
                AssetFileDescriptor f = null;
                try { f = getContext().getAssets().openFd(fname); }
                catch (IOException ignore) {  }
                return f;
            default:
                break;  // Important!  Don't die if you don't recognize it!
        }
        return super.openTypedAssetFile(uri, filter, opts);
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        String fname = uri.getQueryParameter(SenderContract.PARAM_FNAME);
        Log.d(TAG, "openAssetFile: " + fname + "@" + mode + "  <= " + uri);
        switch (MATCHER.match(uri)) {
            case ASSETS_DIR_TYPE:
                AssetFileDescriptor f = null;
                try { f = getContext().getAssets().openFd(fname); }
                catch (IOException ignore) {  }
                return f;
            default:
                break;  // Important!  Don't die if you don't recognize it!
        }
        return super.openAssetFile(uri, mode);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String fname = uri.getQueryParameter(SenderContract.PARAM_FNAME);
        Log.d(TAG, "openFile: " + fname + "@" + mode + "  <= " + uri);
        switch (MATCHER.match(uri)) {
            case FILES_DIR_TYPE:
                return ParcelFileDescriptor.open(new File(fname), ParcelFileDescriptor.MODE_READ_ONLY);
            default:
                break;  // Important!  Don't die if you don't recognize it!
        }
        return super.openFile(uri, mode);
    }

    @Override
    public <T> ParcelFileDescriptor openPipeHelper(Uri uri, String mimeType, Bundle opts, T args, PipeDataWriter<T> func)
        throws FileNotFoundException
    {
        Log.d(TAG, "openPipeHelper @ " + mimeType + " : " + uri);
        return super.openPipeHelper(uri, mimeType, opts, args, func);
    }

    @Override
    public String[] getStreamTypes(Uri uri, String filter) {
        Log.d(TAG, "getStreamTypes @ " + filter + " : " + uri);
        return super.getStreamTypes(uri, filter);
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

    private SQLiteDatabase getDb() { return helper.getWritableDatabase(); }
}
