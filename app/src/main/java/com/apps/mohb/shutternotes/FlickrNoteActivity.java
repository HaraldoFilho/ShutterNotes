/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrNoteActivity.java
 *  Last modified : 6/8/24, 10:58 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.apps.mohb.shutternotes.fragments.dialogs.FlickrNoteTipAlertFragment;
import com.apps.mohb.shutternotes.lists.GearList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class FlickrNoteActivity extends AppCompatActivity
        implements OnMapReadyCallback, FlickrNoteTipAlertFragment.FlickrNoteTipDialogListener {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textTags;
    private GearList gearList;

    private FusedLocationProviderClient fusedLocationClient;
    private double lastLatitude;
    private double lastLongitude;

    SupportMapFragment mapFragment;

    private SharedPreferences settings;
    private SharedPreferences warningFirstShow;

    private Toast mustType;
    private Toast locationUpdated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_note);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        editTextTitle = findViewById(R.id.editTextFlickrNoteTitle);
        editTextDescription = findViewById(R.id.editTextFlickrNoteDescription);
        textTags = findViewById(R.id.tagsView);
        FloatingActionButton fabAddTags = findViewById(R.id.fabAddGearFlickr);

        Button buttonCancel = findViewById(R.id.buttonFlickrNoteCancel);
        Button buttonClear = findViewById(R.id.buttonFlickrNoteClear);
        Button buttonOK = findViewById(R.id.buttonFlickrNoteOk);

        gearList = GearList.getInstance();

        fabAddTags.setOnClickListener(view -> {
            Intent intent = new Intent(this, GearNoteActivity.class);
            intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTE);
            startActivity(intent);
        });

        buttonCancel.setOnClickListener(view -> onBackPressed());

        buttonClear.setOnClickListener(view -> {
            editTextTitle.setText(Constants.EMPTY);
            editTextDescription.setText(Constants.EMPTY);
            textTags.setText(Constants.EMPTY);
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }
        });

        buttonOK.setOnClickListener(view -> {

            String textTitle = editTextTitle.getText().toString();
            String textDescription = editTextDescription.getText().toString();
            if (textDescription.isEmpty()) {
                textDescription = Constants.SPACE;
            }

            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }
            ArrayList<String> tags = gearList.getFlickrTags();
            String textGearList = gearList.getGearListText();

            if (textTitle.equals(Constants.EMPTY)) {
                mustType = Toast.makeText(this, R.string.toast_must_type_title, Toast.LENGTH_SHORT);
                mustType.show();
            } else {
                Intent intent = new Intent(this, FullscreenNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.FLICKR_TITLE, textTitle);
                bundle.putString(Constants.FLICKR_DESCRIPTION, textDescription);
                bundle.putStringArrayList(Constants.FLICKR_TAGS, tags);
                bundle.putDouble(Constants.LATITUDE, lastLatitude);
                bundle.putDouble(Constants.LONGITUDE, lastLongitude);

                String textToShow = textTitle;
                String prefKey = settings.getString(Constants.PREF_KEY_WHAT_SHOW, Constants.PREF_SHOW_TITLE);

                switch (Objects.requireNonNull(prefKey)) {

                    case Constants.PREF_SHOW_DESCRIPTION:
                        textToShow = textDescription;
                        break;

                    case Constants.PREF_SHOW_TAGS:
                        textToShow = textGearList;
                        break;
                }

                if (textToShow.equals(Constants.EMPTY) || textToShow.equals(Constants.SPACE)) {
                    textToShow = textTitle;
                }

                bundle.putString(Constants.KEY_FULL_SCREEN_TEXT, textToShow);
                bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTE);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        warningFirstShow = this.getSharedPreferences(Constants.FLICKR_NOTE_WARNING, Context.MODE_PRIVATE);

        if (warningFirstShow.getBoolean(Constants.KEY_FIRST_SHOW, true)) {
            FlickrNoteTipAlertFragment dialogWarning = new FlickrNoteTipAlertFragment();
            dialogWarning.show(getSupportFragmentManager(), "FlickrNoteTipAlertFragment");
        }

        // Create an instance of GoogleAPIClient to load maps
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // create map
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentFlickrNote);

    }

    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_flickr_note, menu);
        MenuItem menuSyncClock = menu.findItem(R.id.action_sync_clock);
        menuSyncClock.setEnabled(true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Synchronize clock
            case R.id.action_sync_clock: {
                Intent intent = new Intent(this, ClockActivity.class);
                startActivity(intent);
                break;
            }

            // Update location
            case R.id.action_update_location: {
                getLastLocation();
                locationUpdated = Toast.makeText(this, R.string.toast_location_updated, Toast.LENGTH_SHORT);
                if (lastLatitude == Constants.DEFAULT_LATITUDE && lastLongitude == Constants.DEFAULT_LONGITUDE) {
                    locationUpdated.setText(R.string.toast_location_not_updated);
                }
                locationUpdated.show();
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
        if (gearList.getList().isEmpty()) {
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }
        }

        String tags = gearList.getGearListText().replace(Constants.NEW_LINE,
                Constants.SPACE + Constants.SPACE + Constants.SPACE);

        if (!tags.equals(Constants.EMPTY)) {
            textTags.setText(tags);
        } else {
            textTags.setText(Constants.EMPTY);
        }

        getLastLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gearList.getList().clear();
        try {
            gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        String prefKey = settings.getString(Constants.PREF_KEY_MAP_ZOOM_LEVEL, Constants.PREF_MID);

        int zoomLevel;
        double markerOffset;

        switch (Objects.requireNonNull(prefKey)) {

            case Constants.PREF_HIGH:
                zoomLevel = Constants.MAP_HIGH_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_HZ_Y_OFFSET;
                break;

            case Constants.PREF_MID:
                zoomLevel = Constants.MAP_MID_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_MZ_Y_OFFSET;
                break;

            case Constants.PREF_LOW:
                zoomLevel = Constants.MAP_LOW_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_LZ_Y_OFFSET;
                break;

            default:
                zoomLevel = Constants.MAP_NONE_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_NZ_Y_OFFSET;
                break;
        }

        LatLng currentLocation = new LatLng(lastLatitude, lastLongitude);
        LatLng markerLocation = new LatLng(lastLatitude + markerOffset, lastLongitude);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        Marker marker = googleMap.addMarker(new MarkerOptions().position(markerLocation));
        assert marker != null;
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_camera_red_36dp));
        marker.setDraggable(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mustType.cancel();
            locationUpdated.cancel();
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onFlickrNoteTipDialogPositiveClick(DialogFragment dialog) {
        warningFirstShow.edit().putBoolean(Constants.KEY_FIRST_SHOW, false).apply();
    }

    private void getLastLocation() {

        // Get the last user's none location. Most of the times
        // this corresponds to user's current location or very near
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted get the last location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lastLatitude = location.getLatitude();
                            lastLongitude = location.getLongitude();
                        } else {
                            lastLongitude = Constants.DEFAULT_LATITUDE;
                            lastLongitude = Constants.DEFAULT_LONGITUDE;
                        }
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(this);
                        }
                    });
        } else {
            // Check if user already denied permission request
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.FINE_LOCATION_PERMISSION_REQUEST);
            }
        }

    }

}
