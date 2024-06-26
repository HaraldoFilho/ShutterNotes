/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrNote.java
 *  Last modified : 6/26/24, 10:14 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;

import com.apps.mohb.shutternotes.Constants;
import com.flickr4java.flickr.photos.GeoData;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class FlickrNote extends Note {

    private String description;
    private ArrayList<String> tags;
    private double latitude;
    private double longitude;
    private String startTime;
    private String finishTime;
    private boolean selected;

    public FlickrNote(String title, String description, ArrayList<String> tags,
                      double latitude, double longitude, String startTime, String finishTime) {
        super.setText(title);
        this.description = description;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.selected = false;
    }

    public String getTitle() {
        return super.getText();
    }

    public void setTitle(String title) {
        super.setText(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public boolean isInTimeInterval(String time) {

        SimpleDateFormat date = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

        Date startTime = date.parse(this.getStartTime(), new ParsePosition(Constants.INITIAL_POSITION));
        Date finishTime = date.parse(this.getFinishTime(), new ParsePosition(Constants.INITIAL_POSITION));
        Date photoTime = date.parse(time, new ParsePosition(Constants.INITIAL_POSITION));

        if (photoTime != null) {
            return photoTime.after(startTime) && photoTime.before(finishTime);
        } else {
            return false;
        }

    }

    public String[] getTagsArray() {

        String[] tagsArray = new String[tags.size()];
        Iterator<String> iterator = tags.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            tagsArray[i] = iterator.next();
            i++;
        }

        return tagsArray;

    }

    public GeoData getGeoData() {
        GeoData geoData = new GeoData();
        geoData.setLatitude((float) latitude);
        geoData.setLongitude((float) longitude);
        return geoData;
    }

}
