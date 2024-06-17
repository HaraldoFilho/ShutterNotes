/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrAccountActivity.java
 *  Last modified : 6/17/24, 11:02 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flickr4java.flickr.auth.Auth;

import java.util.Objects;


public class FlickrAccountActivity extends AppCompatActivity {

    private WebView flickrWebView;
    private TextView codeTextView;
    private Button connectButton;

    private FlickrApi flickrApi;
    private String tokenKey;

    private int callerActivity;

    private Toast accountConnected;
    private Toast typeCode;
    private Toast wrongCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flickr_account_authorize);
        Objects.requireNonNull(getSupportActionBar()).hide();

        try {

            flickrApi = new FlickrApi(getApplicationContext());

            flickrWebView = findViewById(R.id.webViewFlickrAuth);
            configureWebView(flickrWebView);

            codeTextView = findViewById(R.id.inputTextFlickrAuth);
            codeTextView.setOnFocusChangeListener((view, b) -> {
                codeTextView.setHint(Constants.EMPTY);
                setButtonConnect();
            });

            connectButton = findViewById(R.id.authButtonFlickrAuth);
            connectButton.setClickable(false);
            connectButton.setOnClickListener(view -> {
                int textSize = codeTextView.getText().length();
                if (textSize >= Constants.TOKEN_KEY_SIZE_MIN && textSize <= Constants.TOKEN_KEY_SIZE_MAX) {
                    tokenKey = codeTextView.getText().toString();
                    flickrApi.setTokenKey(tokenKey);
                    setButtonConnecting();
                    if (flickrApi.getAccessToken()) {
                        try {
                            authenticate(flickrApi.checkAuthToken());
                        } catch (Exception e) {
                            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                            flickrApi.clearTokens();
                        }
                    } else {
                        codeTextView.setText(Constants.EMPTY);
                        codeTextView.setHint(R.string.text_type_code);
                        wrongCode = Toast.makeText(this, R.string.toast_wrong_code, Toast.LENGTH_SHORT);
                        wrongCode.show();
                        setButtonConnect();
                    }

                } else {
                    typeCode = Toast.makeText(this, R.string.toast_type_code, Toast.LENGTH_SHORT);
                    typeCode.show();
                }
            });

            callerActivity = getIntent().getIntExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_MAIN);

            if (!flickrApi.getToken().isEmpty() && !flickrApi.getTokenSecret().isEmpty()) {
                authenticate(flickrApi.checkAuthToken());
            } else {
                openAuthorizationUrl();
            }

        } catch (Exception e) {
            showUnableToCommunicate();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            accountConnected.cancel();
            typeCode.cancel();
            wrongCode.cancel();
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    private void authenticate(Auth auth) {
        if (auth == null) {
            if (connectButton.getText()
                    .equals(this.getResources().getString(R.string.button_connecting))) {
                codeTextView.setText(Constants.EMPTY);
                codeTextView.setHint(R.string.text_type_code);
                wrongCode = Toast.makeText(this, R.string.toast_wrong_code, Toast.LENGTH_SHORT);
                wrongCode.show();
                setButtonConnect();
            }
            flickrApi.clearTokens();
            openAuthorizationUrl();
        } else {
            String userId = auth.getUser().getId();
            Log.i(Constants.LOG_INFO_TAG, "User id: " + userId);
            accountConnected = Toast.makeText(this, R.string.toast_account_connected, Toast.LENGTH_SHORT);
            accountConnected.show();

            if (callerActivity != Constants.ACTIVITY_MAIN) {
                onBackPressed();
            } else {
                setContentView(R.layout.activity_flickr_account_connected);
                flickrWebView = findViewById(R.id.webViewFlickrProfile);
                configureWebView(flickrWebView);
                flickrWebView.loadUrl(Constants.FLICKR_URL + userId);
            }
        }
    }

    private void openAuthorizationUrl() {
        String authorizationUrl = flickrApi.getAuthUrl();
        if (!authorizationUrl.isEmpty()) {
            flickrWebView.loadUrl(authorizationUrl);
            setButtonConnect();
        } else {
            showUnableToCommunicate();
        }
    }

    private void showUnableToCommunicate() {
        Toast.makeText(this, R.string.toast_unable_to_communicate, Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void setButtonConnect() {
        connectButton.setClickable(true);
        connectButton.setText(R.string.button_connect);
        connectButton.setBackgroundColor(this.getResources().getColor(R.color.colorBackgroundPink, null));
    }

    private void setButtonConnecting() {
        connectButton.setClickable(false);
        connectButton.setText(R.string.button_connecting);
        connectButton.setBackgroundColor(this.getResources().getColor(R.color.colorBackgroundBlue, null));
    }

    // Insert token key in text field and execute get access token
    private void insertTokenKey(String key) throws Exception {
        codeTextView.setText(key);
        flickrApi.setTokenKey(key);
        setButtonConnecting();
        if (flickrApi.getAccessToken()) {
            authenticate(flickrApi.checkAuthToken());
        } else {
            codeTextView.setText(Constants.EMPTY);
            codeTextView.setHint(R.string.text_type_code);
            wrongCode = Toast.makeText(this, R.string.toast_wrong_code, Toast.LENGTH_SHORT);
            wrongCode.show();
            setButtonConnect();
        }
    }

    // Below this point it was used code from the following page:
    // http://technoranch.blogspot.com/2014/08/how-to-get-html-content-from-android-webview.html

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView(WebView webView) {
        // Sets a customized web view client capable of extract html content from a javascript page
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Javascript code to extract html content from Flickr pages
                // If user is PRO, the token key is in the 9th (index 8) span element
                view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
                        "(document.getElementsByTagName('span')[8].innerHTML);");
                // If user is NOT PRO, the token key is in the 10th (index 9) span element
                view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
                        "(document.getElementsByTagName('span')[9].innerHTML);");
            }

        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.addJavascriptInterface(new AuthJavaScriptInterface(), "HtmlViewer");
    }

    class AuthJavaScriptInterface {

        @JavascriptInterface
        public void getTokenKey(String code) {
            Handler handlerForJavascriptInterface = new Handler(Looper.getMainLooper());
            handlerForJavascriptInterface.post(() -> {
                if (code.length() == Constants.TOKEN_KEY_SIZE_MAX && code.contains(Constants.DASH)) {
                    try {
                        insertTokenKey(code);
                    } catch (Exception e) {
                        Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                    }
                }
            });
        }
    }

}

