/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : ClockActivity.java
 *  Last modified : 6/7/24, 5:36 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import static android.os.SystemClock.sleep;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.concurrent.RecursiveTask;


public class ClockActivity extends AppCompatActivity {

    private TextView timeTextview;
    private TextView dateTextView;
    private Boolean stopClock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Set the navigation bar to the same color of background
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBackgroundDarkGreen, null));

        timeTextview = findViewById(R.id.textTime);
        dateTextView = findViewById(R.id.textDate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopClock = false;
        new UpdateClock().compute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopClock = true;
    }

    private class UpdateClock extends RecursiveTask<Void> {

        UpdateClock() {

            Date currentTime = Calendar.getInstance().getTime();
            String timeText = SimpleDateFormat.getTimeInstance().format(currentTime);
            String date = SimpleDateFormat.getDateInstance().format(currentTime);
            String timeZone = String.valueOf(SimpleTimeZone.getDefault().getRawOffset() / Constants.MS_PER_HOUR);
            String dateText = date + Constants.UTC + timeZone;

            runOnUiThread(() -> {
                timeTextview.setText(timeText);
                dateTextView.setText(dateText);
            });

        }

        @Override
        protected Void compute() {

            if (stopClock) {
                return null;
            }

            UpdateClock updateClock = new UpdateClock();
            updateClock.fork();

            sleep(100);

            return null;

        }
    }

}