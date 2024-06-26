/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : MainActivity.java
 *  Last modified : 6/26/24, 10:14 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        Button buttonSimpleNote = findViewById(R.id.buttonSimpleNote);
        Button buttonGearNote = findViewById(R.id.buttonGearNote);
        Button buttonFlickrNote = findViewById(R.id.buttonFlickrNote);

        buttonSimpleNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SimpleNoteActivity.class);
            startActivity(intent);
        });

        buttonGearNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), GearNoteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_GEAR_NOTE);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        buttonFlickrNote.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), FlickrNoteActivity.class);
            startActivity(intent);
        });

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        CharSequence name = getString(R.string.notify_channel_name);
        String description = getString(R.string.notify_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel;
        channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        switch (id) {

            // Simple Notes
            case R.id.nav_simple:
                intent = new Intent(this, SimpleNotesListActivity.class);
                break;

            // Gear Notes
            case R.id.nav_gear:
                intent = new Intent(this, GearNotesListActivity.class);
                break;

            // Flickr Notes
            case R.id.nav_flickr:
                intent = new Intent(this, FlickrNotesListActivity.class);
                break;

            // Archived
            case R.id.nav_archive:
                intent = new Intent(this, ArchiveActivity.class);
                break;

            // Account
            case R.id.nav_account:
                intent = new Intent(this, FlickrAccountActivity.class);
                intent.putExtra("CALLER_ACTIVITY", 0);
                break;

            // Clock
            case R.id.nav_clock:
                intent = new Intent(this, ClockActivity.class);
                break;

            // Settings
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;

            // Help
            case R.id.nav_help:
                intent = new Intent(this, HelpActivity.class);
                intent.putExtra(Constants.KEY_URL, getString(R.string.url_help));
                break;

            // About
            case R.id.nav_about:
                intent = new Intent(this, AboutActivity.class);
                break;

        }

        if (intent != null) {
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
