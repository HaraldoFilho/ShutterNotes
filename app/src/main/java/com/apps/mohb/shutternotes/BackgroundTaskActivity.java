/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : BackgroundTaskActivity.java
 *  Last modified : 6/7/24, 5:59 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import androidx.appcompat.app.AppCompatActivity;

public class BackgroundTaskActivity extends AppCompatActivity {

    /*
     * Class to replace deprecated AsyncTask
     */

    protected class BackgroundTask extends Thread {

        private void beforeMainThread() {
            runOnUiThread(this::onPreExecute);
        }

        private void mainThread() {
            doInBackground();
        }

        private void afterMainThread() {
            runOnUiThread(this::onPostExecute);
        }

        protected void onPreExecute() {
        }

        protected void doInBackground() {
        }

        protected void onPostExecute() {
        }

        @Override
        public void run() {
            beforeMainThread();
            mainThread();
            afterMainThread();
        }

    }
}