package com.twitter.university.android.tweettest;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class SendActivity extends Activity {
    private static final String TWEET = "Testing!  1, 2, 3!";
    private static final Uri LARRY_URL = Uri.parse("https://g.twimg.com/Twitter_logo_blue.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sender);

        findViewById(R.id.simple_text).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.simple_text); }
        });
        findViewById(R.id.quoted).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.quoted); }
        });
        findViewById(R.id.structured).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.structured); }
        });
        findViewById(R.id.bad_immediate).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.bad_immediate); }
        });
        findViewById(R.id.good_immediate).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.good_immediate); }
        });
        findViewById(R.id.bad_typed_asset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.bad_typed_asset); }
        });
        findViewById(R.id.good_typed_asset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.good_typed_asset); }
        });
        findViewById(R.id.bad_asset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.bad_asset); }
        });
        findViewById(R.id.good_asset).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.good_asset); }
        });
        findViewById(R.id.bad_file).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.bad_file); }
        });
        findViewById(R.id.good_file).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.good_file); }
        });
        findViewById(R.id.bad_file_ref).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.bad_file_ref); }
        });
        findViewById(R.id.good_file_ref).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { sendData(R.id.good_file_ref); }
        });
    }

    void sendData(int selector) {
        switch (selector) {
            case R.id.simple_text:
                Log.d("TEST", "Send: text");
                TwitterContract.tweet(this, TWEET);
                break;

            case R.id.quoted:
                Log.d("TEST", "Send: quoted");
                TwitterContract.tweetQuoted(this, TWEET, "someguy");
                break;

            case R.id.structured:
                Log.d("TEST", "Send: structured");
                TwitterContract.tweetStructured(this, TWEET, LARRY_URL, "larry,twitter", "someguy", 666L);
                break;

            case R.id.bad_immediate:
                send(App.BAD_IMMEDIATE);
                break;

            case R.id.good_immediate:
                send(App.GOOD_IMMEDIATE);
                break;

            case R.id.bad_typed_asset:
                send(App.BAD_TYPED_ASSET);
                break;

            case R.id.good_typed_asset:
                send(App.GOOD_TYPED_ASSET);
                break;

            case R.id.bad_asset:
                send(App.BAD_ASSET);
                break;

            case R.id.good_asset:
                send(App.GOOD_ASSET);
                break;

            case R.id.bad_file:
                send(App.BAD_FILE_DESC);
                break;

            case R.id.good_file:
                send(App.GOOD_FILE_DESC);
                break;

            case R.id.bad_file_ref:
                send(App.BAD_REF);
                break;

            case R.id.good_file_ref:
                send(App.GOOD_REF);
                break;
        }
    }

    private void send(Uri uri) {
        Log.d("TEST", "Send: " + uri);
        TwitterContract.tweet(this, TWEET, uri, "png");
    }
}
