/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : HelpActivity.java
 *  Last modified : 6/13/24, 3:52 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class HelpActivity extends AppCompatActivity {

    private WebView webView;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();

        // create webView that will show options_help page
        webView = new WebView(this);
        setContentView(webView);
        configureWebView(webView);
        webView.setWebViewClient(new WebViewClient());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // load options_help page in webView
        String helpUrl = getString(R.string.url_website) + bundle.getString(Constants.KEY_URL);
        Log.i(Constants.LOG_INFO_TAG, "Help url: " + helpUrl);
        webView.loadUrl(helpUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // cancel toast if page if exit options_help screen
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_send_question) {
            String[] address = new String[Constants.FEEDBACK_ARRAY_SIZE];
            address[Constants.LIST_HEAD] = getString(R.string.info_help_email);
            composeEmail(address, getString(R.string.action_question) + " " + getString(R.string.action_about_application)
                    + " " + getString(R.string.info_app_name));
        }

        return super.onOptionsItemSelected(item);
    }

    // Compose a e-mail to send a question
    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(intent);
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


}
