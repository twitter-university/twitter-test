/*
 * Copyright (c) 2014 Twitter Inc.
 */
package com.twitter.university.android.tweettest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;


public class TwitterContract {
    public static final int CONTRACT_VERSION = 1;

    /** MIME text type */
    public static final String MIME_TEXT = "text/plain";
    /** MIME prefix for image data */
    public static final String MIME_IMAGE = "image/";

    /** Intent URI scheme to use when invoking twitter actions. */
    public static final String SCHEME = "twitter";

    /** The host part of twitter://post?message=xxx */
    public static final String HOST_POST = "post";
    /** The host part of twitter://quote?message=xxx&screen_name=xxx */
    public static final String HOST_QUOTE = "quote";

    /** Query parameter: tweet text. */
    public static final String MESSAGE = "message";
    /** Query parameter: tweet text. Synonym for {@link #MESSAGE} */
     public static final String TEXT = "text";
     /** QUOTE query parameter: screen name of the user whose tweet is being quoted. */
    public static final String SCREEN_NAME = "screen_name";
     /** POST query parameter: reply status id */
    public static final String IN_REPLY_TO = "in_reply_to_status_id";
    /** POST query parameter: URL embedded in the tweet. */
    public static final String URL = "url";
    /** POST query parameter: hashtags embedded in the tweet. */
    public static final String HASHTAGS = "hashtags";
    /** POST query parameter: User that is the original source of the tweet. */
    public static final String VIA = "via";

    /** Twitter app package */
    public static final String TWITTER_PKG = "com.twitter.android";

    /** URI for the Google Play app */
    public static final Uri PLAY_STORE_APP = Uri.parse("market://details");
    /** URI for the Google Play web app */
    public static final Uri PLAY_STORE_WEB
        = Uri.parse("http://play.google.com/store/apps/details");

    /** URI for the Twitter app in the Google Play app */
    public static final Uri TWITTER_AT_PLAY_APP
        = PLAY_STORE_APP.buildUpon().appendQueryParameter("id", TWITTER_PKG).build();
    /** URI for the Twitter app in the Google Play web app */
    public static final Uri TWITTER_AT_PLAY_WEB
        = PLAY_STORE_WEB.buildUpon().appendQueryParameter("id", TWITTER_PKG).build();

    /** Toast when the Twitter app can't be found */
    public static final String NOT_INSTALLED
        = "The Twitter application is not installed.\n  Please download it from the PlayStore";

    public static final class Columns {
        private Columns() { }
        static final String DATA = "_data";
    }

    /**
     * Tweet simple text.
     *
     * @param ctxt a context
     * @param tweet the text to tweet
     * @return true iff successful
     * @throws IllegalArgumentException if tweet is empty
     */
    public static boolean tweet(Context ctxt, String tweet) {
        if (TextUtils.isEmpty(tweet)) {
            throw new IllegalArgumentException("empty tweet");
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, tweet);
        i.setType(MIME_TEXT);

        return tweet(ctxt, i);
    }

    /**
     * Post a tweet with added media.
     *
     * The Twitter app will process a media references in the following order,
     * using the first one that works:
     * 1) a file:// URL.  The URL must point to an accessible media file
     * n) a content:// URL.  This is a reference to a content provider in your application.
     *
     * Your content provider's implementation of getType must return a recognizable
     * MIME type for the content URL you pass to the Twitter app.
     *
     * Your content provider will receive queries, in the order described below,
     * until the Twitter app finds one that works.  If your content provider
     * fails in response to any of the queries, the Twitter app stops its search
     * and considers the image not found.
     *
     * 2) Query the URI, for the single column "_data".
     *    The Twitter app interprets the value in that column
     *    as a file path and attempts to open it.
     * 3) Request a TypedAsset (openTypedAssetFile), using the URI
     * 4) Request an Asset (openAssetFile), using the URI
     * 5) Request a File (openFile), using the URI
     *
     * @param ctxt a context
     * @param tweet the text to tweet. May be null.
     * @param image see the discussion above. May be null.
     * @param imageType the type of the image: jpeg, png, etc...
     * @return true iff successful
     * @throws IllegalArgumentException if tweet and image are both empty
     *         or if a non-null image has no type
     */
    public static boolean tweet(Context ctxt, String tweet, Uri image, String imageType) {
        if (TextUtils.isEmpty(tweet) && (null == image)) {
            throw new IllegalArgumentException("empty tweet");
        }
        if ((null != image) && TextUtils.isEmpty(imageType)) {
            throw new IllegalArgumentException("image must have a type");
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(tweet)) { i.putExtra(Intent.EXTRA_TEXT, tweet); }
        if ((null == image)) { i.setType(MIME_TEXT); }
        else {
            i.putExtra(Intent.EXTRA_STREAM, image);
            i.setType(MIME_IMAGE + imageType);
        }

        return tweet(ctxt, i);
    }

    /**
     * Post a quoted tweet.
     *
     * @param ctxt a context
     * @param tweet the text to tweet
     * @param handle name of the user whose tweet is being quoted
     * @return true iff successful
     * @throws IllegalArgumentException if tweet and image are both empty
     *         or if a non-null image has no type
     */
    public static boolean tweetQuoted(Context ctxt, String tweet, String handle) {
        if (TextUtils.isEmpty(handle)) {
            throw new IllegalArgumentException("empty screen name for quoted tweet");
        }
        return tweetActionView(ctxt, getBaseUri(HOST_QUOTE, tweet)
            .appendQueryParameter(SCREEN_NAME, handle).build());
    }

    /**
     * Post a tweet with a complex structure.
     *
     * @param ctxt a context
     * @param tweet the text to tweet
     * @param url an embedded URL. May be null.
     * @param hashtags embedded hashtags. May be null.
     * @param via original source of the tweet. May be null.
     * @param replyTo reply status id.  Ignored if <= 0.
     * @return true iff successful
     */
    public static boolean tweetStructured(
        Context ctxt,
        String tweet,
        Uri url,
        String hashtags,
        String via,
        long replyTo)
    {
        Uri.Builder uri = getBaseUri(HOST_POST, tweet);
        if (null != url) { uri.appendQueryParameter(URL, url.toString()); }
        if (!TextUtils.isEmpty(via)) { uri.appendQueryParameter(VIA, via); }
        if (!TextUtils.isEmpty(hashtags)) {
            uri.appendQueryParameter(HASHTAGS, hashtags);
        }
        if (0 >= replyTo) {
            uri.appendQueryParameter(IN_REPLY_TO, String.valueOf(replyTo));
        }
        return tweetActionView(ctxt, uri.build());
    }

    /**
     * Fire an intent to interact with the Twitter application.
     * If the application is not currently installed,
     * attempt to direct the user to install it.
     *
     * @param ctxt a context
     * @param intent the fully constructed Twitter intent
     * @return true iff successful
     */
    public static boolean tweet(Context ctxt, Intent intent) {
        intent.setPackage(TWITTER_PKG);

        try {
            ctxt.startActivity(intent);
            return true;
        }
        catch (ActivityNotFoundException ignore1) {
            Toast.makeText(ctxt, NOT_INSTALLED, Toast.LENGTH_SHORT).show();
            try { ctxt.startActivity(new Intent(Intent.ACTION_VIEW, TWITTER_AT_PLAY_APP)); }
            catch (ActivityNotFoundException ignore2) {
                try { ctxt.startActivity(new Intent(Intent.ACTION_VIEW, TWITTER_AT_PLAY_WEB)); }
                catch (ActivityNotFoundException ignore3) { }
            }
        }

        return false;
    }

    private static Uri.Builder getBaseUri(String host, String tweet) {
        if (TextUtils.isEmpty(tweet)) {
            throw new IllegalArgumentException("empty tweet");
        }
        Uri.Builder uri = new Uri.Builder().scheme(SCHEME).authority(host);
        uri.appendQueryParameter(TEXT, tweet);
        return uri;
    }

    private static boolean tweetActionView(Context ctxt, Uri uri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(uri);
        return tweet(ctxt, i);
    }
}
