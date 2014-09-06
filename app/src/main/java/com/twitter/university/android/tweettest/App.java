/*
 * Copyright (c) 2014 Twitter Inc.
 */
package com.twitter.university.android.tweettest;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class App extends Application {
    private static final String TAG = "APP";

    private static final String BORK = "bork.png";
    private static final String LARRY = "larry.png";

    // file by path
    public static final String BAD_FILE = new File(getPicsDir(), BORK).toString();
    public static final String GOOD_FILE = new File(getPicsDir(), App.LARRY).toString();

    // file by URI
    public static final Uri BAD_IMMEDIATE = Uri.parse("file://" + BAD_FILE);
    public static final Uri GOOD_IMMEDIATE = Uri.parse("file://" + GOOD_FILE);

    // CP provided typed asset
    public static final Uri BAD_TYPED_ASSET = SenderContract.TypedAssets.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, BORK).build();
    public static final Uri GOOD_TYPED_ASSET = SenderContract.TypedAssets.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, App.LARRY).build();

    // CP provided asset
    public static final Uri BAD_ASSET = SenderContract.Assets.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, BORK).build();
    public static final Uri GOOD_ASSET = SenderContract.Assets.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, App.LARRY).build();

    // CP provided file
    public static final Uri BAD_FILE_DESC = SenderContract.Assets.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, BORK).build();
    public static final Uri GOOD_FILE_DESC = SenderContract.Files.URI.buildUpon()
        .appendQueryParameter(SenderContract.PARAM_FNAME, new File(getPicsDir(), App.LARRY).toString()).build();

    // CP provided ref to file (see DbHelper)
    public static final int ID_BAD_REF = 1;
    public static final Uri BAD_REF = SenderContract.Extras.URI.buildUpon()
        .appendPath(String.valueOf(App.ID_BAD_REF)).build();
    public static final int ID_GOOD_REF = 2;
    public static final Uri GOOD_REF = SenderContract.Extras.URI.buildUpon()
        .appendPath(String.valueOf(App.ID_GOOD_REF)).build();

    private static class CopyTask extends AsyncTask<String, Void, Void> {
        private final Context ctxt;
        public CopyTask(Context ctxt)  { this.ctxt = ctxt; }

        @Override
        protected Void doInBackground(String... strings) {
            File image = new File(getPicsDir(), LARRY);
            boolean exists = image.exists();
            Log.d(TAG, "create file: " + image + ": " + exists);
            if (exists) { return null; }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = ctxt.getAssets().open(LARRY);
                out = new FileOutputStream(image);
                int len;
                byte[] buf = new byte[1024];
                while (0 < (len = in.read(buf))) { out.write(buf, 0, len); }

            }
            catch (IOException e) {
                Log.e(TAG, "Copy failed: " + e, e);
            }
            finally {
                if (null != in) {
                    try { in.close(); } catch (IOException ignore) { }
                }
                if (null != out) {
                    try { out.close(); } catch (IOException ignore) { }
                }
            }

            return null;
        }
    }

    private static File getPicsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new CopyTask(this).execute(LARRY);
    }
}
