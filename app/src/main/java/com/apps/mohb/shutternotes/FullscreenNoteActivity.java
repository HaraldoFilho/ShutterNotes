/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FullscreenNoteActivity.java
 *  Last modified : 6/17/24, 9:46 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.apps.mohb.shutternotes.fragments.dialogs.FullscreenTipAlertFragment;
import com.apps.mohb.shutternotes.lists.Notebook;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.SimpleNote;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenNoteActivity extends AppCompatActivity
        implements FullscreenTipAlertFragment.FullscreenTipDialogListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.getMainLooper());
    private int state;

    private Notebook notebook;
    private String text;
    private int callerActivity;
    private TextView textView;

    private SimpleDateFormat date;
    private String startTime = Constants.EMPTY;
    private String finishTime = Constants.EMPTY;

    private SharedPreferences instructionsFirstShow;


    private final Runnable mHidePart2Runnable = () -> {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = textView.getWindowInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            textView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = () -> {
        // Delayed display of UI elements
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fullscreen_note);

        notebook = Notebook.getInstance(getApplicationContext());

        mVisible = true;

        callerActivity = Objects.requireNonNull(getIntent().getExtras()).getInt(Constants.KEY_CALLER_ACTIVITY);

        textView = findViewById(R.id.textFullscreen);

        if (callerActivity == Constants.ACTIVITY_GEAR_NOTE) {
            textView.setLineSpacing(0, (float) Constants.FULL_SCREEN_TEXT_LINE_SPACING);
        }

        text = getIntent().getExtras().getString(Constants.KEY_FULL_SCREEN_TEXT);

        textView.setText(Objects.requireNonNull(text).trim());
        textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundGreen, null));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorBackgroundGreen, null));

        String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

        switch (Objects.requireNonNull(prefKey)) {

            case Constants.PREF_FONT_SIZE_SMALL:
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_SMALL_LARGE);
                break;

            case Constants.PREF_FONT_SIZE_MEDIUM:
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_MEDIUM_LARGE);
                break;

            case Constants.PREF_FONT_SIZE_LARGE:
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_LARGE_LARGE);
                break;

        }

        state = Constants.STATE_COLOR_GREEN;
        date = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        startTime = date.format(new Date().getTime());

        // Set up the user interaction to manually show or hide the system UI.
        textView.setOnLongClickListener(view -> {
            toggle();
            return true;
        });

        instructionsFirstShow = this.getSharedPreferences(Constants.FULLSCREEN_INSTRUCTIONS, Context.MODE_PRIVATE);

        if (instructionsFirstShow.getBoolean(Constants.KEY_FIRST_SHOW, true)) {
            FullscreenTipAlertFragment dialogInstructions = new FullscreenTipAlertFragment();
            dialogInstructions.show(getSupportFragmentManager(), "FullscreenTipAlertFragment");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        hide();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {

        if (mVisible) {
            textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundGreen, null));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBackgroundGreen, null));
        } else {

            switch (state) {

                case Constants.STATE_COLOR_GREEN:
                    textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundRed, null));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorBackgroundRed, null));
                    finishTime = date.format(new Date().getTime());
                    state = Constants.STATE_COLOR_RED;
                    break;

                case Constants.STATE_COLOR_RED:

                    String lastNote = Constants.EMPTY;

                    switch (callerActivity) {

                        case Constants.ACTIVITY_SIMPLE_NOTE:
                            SimpleNote simpleNote = new SimpleNote(text);
                            if (!notebook.getSimpleNotes().isEmpty()) {
                                lastNote = notebook.getSimpleNotes().get(Constants.LIST_HEAD).getText();
                            }
                            if (!simpleNote.getText().equals(lastNote)) {
                                notebook.addNote(simpleNote);
                            }
                            break;

                        case Constants.ACTIVITY_GEAR_NOTE:
                            GearNote gearNote = new GearNote(text);
                            if (!notebook.getGearNotes().isEmpty()) {
                                lastNote = notebook.getGearNotes().get(Constants.LIST_HEAD).getGearList();
                            }
                            if (!gearNote.getGearList().equals(lastNote)) {
                                notebook.addNote(gearNote);
                            }
                            break;

                        case Constants.ACTIVITY_FLICKR_NOTE:
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null) {
                                String title = bundle.getString(Constants.FLICKR_TITLE);
                                String description = bundle.getString(Constants.FLICKR_DESCRIPTION);
                                ArrayList<String> tags = bundle.getStringArrayList(Constants.FLICKR_TAGS);
                                double latitude = bundle.getDouble(Constants.LATITUDE);
                                double longitude = bundle.getDouble(Constants.LONGITUDE);
                                FlickrNote flickrNote = new FlickrNote(title, description, tags,
                                        latitude, longitude, startTime, finishTime);
                                notebook.addNote(flickrNote);
                            }
                            break;

                    }
                    // go back when red screen is touched
                    super.onBackPressed();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // do not go back when back button is pressed
        super.onBackPressed();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            notebook.saveState();
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    public void onFullscreenTipDialogPositiveClick(DialogFragment dialog) {
        instructionsFirstShow.edit().putBoolean(Constants.KEY_FIRST_SHOW, false).apply();
    }

}
