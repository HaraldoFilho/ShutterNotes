/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : Archive.java
 *  Last modified : 6/8/24, 10:58 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.lists;

import android.content.Context;
import android.util.Log;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.SimpleNote;

import java.io.IOException;
import java.util.ArrayList;


public class Archive {

    private static ArrayList<SimpleNote> simpleNotes;
    private static ArrayList<GearNote> gearNotes;
    private static ArrayList<FlickrNote> flickrNotes;
    private static SavedState archiveSavedState;

    private static Archive archive;

    private Archive(Context context) {
        archiveSavedState = new SavedState(context, Constants.ARCHIVE_SAVED_STATE);
        simpleNotes = new ArrayList<>();
        gearNotes = new ArrayList<>();
        flickrNotes = new ArrayList<>();
    }

    public static Archive getInstance(Context context) {
        if (archive == null) {
            archive = new Archive(context);
        }
        try {
            loadState();
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
        return archive;
    }

    private static void loadState() throws IOException {
        simpleNotes = archiveSavedState.getSimpleNotesState();
        gearNotes = archiveSavedState.getGearNotesState();
        flickrNotes = archiveSavedState.getFlickrNotesState();
    }

    public void saveState() throws IOException {
        archiveSavedState.setSimpleNotesState(simpleNotes);
        archiveSavedState.setGearNotesState(gearNotes);
        archiveSavedState.setFlickrNotesState(flickrNotes);
    }

    // Simple Notes methods

    public ArrayList<SimpleNote> getSimpleNotes() {
        return simpleNotes;
    }

    public void addNote(SimpleNote note) {
        simpleNotes.add(Constants.LIST_HEAD, note);
    }

    public void removeSimpleNote(int position) {
        simpleNotes.remove(position);
    }

    // Gear Notes methods

    public ArrayList<GearNote> getGearNotes() {
        return gearNotes;
    }

    public void addNote(GearNote note) {
        gearNotes.add(Constants.LIST_HEAD, note);
    }

    public void removeGearNote(int position) {
        gearNotes.remove(position);
    }

    // Flickr Notes methods

    public ArrayList<FlickrNote> getFlickrNotes() {
        return flickrNotes;
    }

    public void addNote(FlickrNote note) {
        note.setSelected(false);
        flickrNotes.add(Constants.LIST_HEAD, note);
    }

    public void removeFlickrNote(int position) {
        flickrNotes.remove(position);
    }


}
