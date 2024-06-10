/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : WebsiteActivity.java
 *  Last modified : 6/9/24, 4:05 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class WebsiteActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create webView that will show options_help page
        webView = new WebView(this);
        setContentView(webView);
        configureWebView(webView);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView(WebView webView) {
        // Sets a customized web view client capable of extract html content from a javascript page
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Javascript code to extract html content from Flickr pages
                // If user is PRO, the token key is in the 7th (index 6) span element
                view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
                        "(document.getElementsByTagName('span')[6].innerHTML);");
                // If user is NOT PRO, the token key is in the 8th (index 7) span element
                view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
                        "(document.getElementsByTagName('span')[7].innerHTML);");
            }

        });
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load website in webView
        webView.loadUrl(getString(R.string.url_website));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            // If can, go back
            // to the previous page
            webView.goBack();
        } else {
            super.onBackPressed();
        }

    }

}
