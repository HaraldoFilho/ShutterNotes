/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrApi.java
 *  Last modified : 6/26/24, 10:14 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class FlickrApi {

    private static Flickr flickr;
    private static String apiSecret;
    private static String token;
    private static String tokenKey;
    private static String tokenSecret;
    private static OAuth1RequestToken requestToken;
    private static OAuth1Token accessToken;

    private static SharedPreferences flickrAccount;

    static Auth auth;
    static String authUrl;
    static boolean gotAccess;


    public FlickrApi(Context context) {

        String apiKey = context.getResources().getString(R.string.flickr_key);
        apiSecret = context.getResources().getString(R.string.flickr_secret);

        flickr = new Flickr(apiKey, apiSecret, new REST());

        if (flickr.getAuthInterface() != null) {
            Log.i(Constants.LOG_INFO_TAG, "Successfully acquired authentication interface");
        } else {
            Log.e(Constants.LOG_ERROR_TAG, "ERROR: Unable to acquire authentication interface");
        }

        flickrAccount = context.getSharedPreferences(Constants.FLICKR_ACCOUNT, Context.MODE_PRIVATE);

        token = flickrAccount.getString(Constants.TOKEN, Constants.EMPTY);
        tokenSecret = flickrAccount.getString(Constants.TOKEN_SECRET, Constants.EMPTY);

    }

    public String getApiSecret() {
        return apiSecret;
    }

    public Flickr getFlickrInterface() {
        return flickr;
    }

    public void clearTokens() {
        SharedPreferences.Editor flickrAccountEditor = flickrAccount.edit();
        flickrAccountEditor.putString(Constants.TOKEN, Constants.EMPTY);
        flickrAccountEditor.putString(Constants.TOKEN_SECRET, Constants.EMPTY);
        flickrAccountEditor.apply();
    }

    public String getToken() {
        return token;
    }

    public void setTokenKey(String key) {
        tokenKey = key;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public Auth checkAuthToken() {

        ExecutorService executorAuth;
        Future<Auth> futureAuth;

        try {
            executorAuth = Executors.newSingleThreadExecutor();
            futureAuth = executorAuth.submit(new GetAuth());
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            return null;
        }

        try {
            auth = futureAuth.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            futureAuth.cancel(true);
            Log.e(Constants.LOG_ERROR_TAG, "ERROR: Authentication failed");
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        executorAuth.shutdownNow();
        return auth;

    }

    public String getAuthUrl() {

        ExecutorService executorUrl;
        Future<String> futureUrl;

        try {
            executorUrl = Executors.newSingleThreadExecutor();
            futureUrl = executorUrl.submit(new GetAuthorizationUrl());
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            return Constants.EMPTY;
        }

        try {
            authUrl = futureUrl.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            futureUrl.cancel(true);
            authUrl = Constants.EMPTY;
            Log.e(Constants.LOG_ERROR_TAG, "ERROR: Unable to get authorization url");
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        executorUrl.shutdownNow();

        if (!authUrl.isEmpty()) {
            return authUrl;
        } else {
            return Constants.EMPTY;
        }

    }

    public boolean getAccessToken() {

        ExecutorService executorToken;
        Future<Boolean> futureToken;

        try {
            executorToken = Executors.newSingleThreadExecutor();
            futureToken = executorToken.submit(new GetAccessToken());
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            return false;
        }

        try {
            gotAccess = futureToken.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            futureToken.cancel(true);
            Log.e(Constants.LOG_ERROR_TAG, "ERROR: Unable to get token");
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        executorToken.shutdownNow();
        return gotAccess;

    }


    /*
     * Inner classes to deal with Flickr authentication
     */

    private static class GetAuth implements Callable<Auth> {

        @Override
        public Auth call() {

            if (!token.isEmpty() && !tokenSecret.isEmpty()) {
                Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
                Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);
                Log.i(Constants.LOG_INFO_TAG, "Checking token...");
            } else {
                Log.e(Constants.LOG_ERROR_TAG, "ERROR: Token not found");
            }

            try {
                auth = flickr.getAuthInterface().checkToken(token, tokenSecret);
                Log.i(Constants.LOG_INFO_TAG, "Token is valid!");
                Log.i(Constants.LOG_INFO_TAG, "Account connected!");
                Log.i(Constants.LOG_INFO_TAG, "User name: " + auth.getUser().getUsername());
            } catch (FlickrException flickrException) {
                if (!token.isEmpty() && !tokenSecret.isEmpty()) {
                    Log.e(Constants.LOG_ERROR_TAG, "ERROR: Invalid Token");
                }
                auth = null;
                requestToken = null;
                accessToken = null;
                token = Constants.EMPTY;
                tokenSecret = Constants.EMPTY;
            }

            return auth;
        }
    }

    private static class GetAuthorizationUrl implements Callable<String> {

        @Override
        public String call() {
            Log.i(Constants.LOG_INFO_TAG, "Requesting authorization token...");

            requestToken = flickr.getAuthInterface().getRequestToken();
            Log.i(Constants.LOG_INFO_TAG, "Token acquired: " + requestToken);

            String authorizationUrl = flickr.getAuthInterface().getAuthorizationUrl(requestToken, Permission.WRITE);
            Log.i(Constants.LOG_INFO_TAG, "Authorization url: " + authorizationUrl);

            return authorizationUrl;
        }
    }

    private static class GetAccessToken implements Callable<Boolean> {

        boolean success = true;

        public Boolean call() {


            Log.i(Constants.LOG_INFO_TAG, "Requesting access token...");
            Log.i(Constants.LOG_INFO_TAG, "Authorization token: " + requestToken);
            Log.i(Constants.LOG_INFO_TAG, "Token Key: " + tokenKey);

            if (!requestToken.isEmpty() && !tokenKey.isEmpty()) {
                try {
                    accessToken = flickr.getAuthInterface().getAccessToken(requestToken, tokenKey);
                    token = accessToken.getToken();
                    tokenSecret = accessToken.getTokenSecret();

                    SharedPreferences.Editor flickrAccountEditor = flickrAccount.edit();
                    flickrAccountEditor.putString(Constants.TOKEN, token);
                    flickrAccountEditor.putString(Constants.TOKEN_SECRET, tokenSecret);
                    flickrAccountEditor.apply();

                } catch (RuntimeException e) {
                    success = false;
                }
            }

            return success;
        }
    }

}