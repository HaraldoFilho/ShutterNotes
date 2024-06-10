/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : SavedState.java
 *  Last modified : 6/8/24, 10:58 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.lists;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.SimpleNote;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Objects;


public class SavedState {

    private final SharedPreferences savedState;
    private final String dataType;

    public SavedState(Context context, String dataType) {
        this.dataType = dataType;
        savedState = context.getSharedPreferences(this.dataType, Context.MODE_PRIVATE);
    }

    // ### SIMPLE NOTES ###

    // save simple notes list on memory through a json string
    public void setSimpleNotesState(ArrayList<SimpleNote> notes) throws IOException {
        String jsonSimpleNotes = writeSimpleNotesJsonString(notes);
        savedState.edit().putString(Constants.SIMPLE_NOTES, jsonSimpleNotes).apply();
    }

    // get simple notes list from memory through a json string
    // if list was not saved yet creates a new array list
    public ArrayList<SimpleNote> getSimpleNotesState() throws IOException {
        String jsonSimpleNotes = savedState.getString(Constants.SIMPLE_NOTES, Constants.EMPTY);
        if (Objects.requireNonNull(jsonSimpleNotes).equals(Constants.EMPTY)) {
            return new ArrayList<>();
        } else {
            return readSimpleNotesJsonString(jsonSimpleNotes);
        }
    }

    // get a json string of simple notes list from memory
    public String getSimpleNotesJsonState() {
        return savedState.getString(Constants.SIMPLE_NOTES, Constants.EMPTY);
    }


    // create a json string of a list of simple note items
    public String writeSimpleNotesJsonString(ArrayList<SimpleNote> simpleNotes) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(Constants.SPACE);
        writeSimpleNotesArrayList(jsonWriter, simpleNotes);
        jsonWriter.close();
        return stringWriter.toString();
    }

    // write all simple notes to json string
    public void writeSimpleNotesArrayList(JsonWriter writer, ArrayList<SimpleNote> simpleNotes) throws IOException {
        writer.beginArray();
        for (SimpleNote simpleNote : simpleNotes) {
            writeSimpleNote(writer, simpleNote);
        }
        writer.endArray();
    }

    // write a single simple note to json string
    public void writeSimpleNote(JsonWriter writer, SimpleNote simpleNote) throws IOException {
        writer.beginObject();
        writer.name(Constants.JSON_TEXT).value(simpleNote.getText());
        writer.endObject();
    }

    // read a json string containing a list of simple notes
    public ArrayList<SimpleNote> readSimpleNotesJsonString(String jsonString) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new StringReader(jsonString))) {
            return readSimpleNotesArrayList(jsonReader);
        }
    }

    // read a list of simple notes from a json string
    public ArrayList<SimpleNote> readSimpleNotesArrayList(JsonReader jsonReader) throws IOException {
        ArrayList<SimpleNote> simpleNotes = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            simpleNotes.add(readSimpleNote(jsonReader));
        }
        jsonReader.endArray();
        return simpleNotes;
    }

    // read a single simple note from a json string
    public SimpleNote readSimpleNote(JsonReader jsonReader) throws IOException {
        String noteText = Constants.EMPTY;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            if (Constants.JSON_TEXT.equals(jsonReader.nextName())) {
                noteText = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }

        }
        jsonReader.endObject();
        return new SimpleNote(noteText);
    }

    // ### GEAR NOTES ###

    // save gear notes list on memory through a json string
    public void setGearNotesState(ArrayList<GearNote> notes) throws IOException {
        String jsonGearNotes = writeGearNotesJsonString(notes);
        savedState.edit().putString(Constants.GEAR_NOTES, jsonGearNotes).apply();
    }

    // get gear notes list from memory through a json string
    // if list was not saved yet creates a new array list
    public ArrayList<GearNote> getGearNotesState() throws IOException {
        String jsonGearNotes = savedState.getString(Constants.GEAR_NOTES, Constants.EMPTY);
        if (Objects.requireNonNull(jsonGearNotes).equals(Constants.EMPTY)) {
            return new ArrayList<>();
        } else {
            return readGearNotesJsonString(jsonGearNotes);
        }
    }

    // get a json string of gear notes list from memory
    public String getGearNotesJsonState() {
        return savedState.getString(Constants.GEAR_NOTES, Constants.EMPTY);
    }

    // create a json string of a list of gear note items
    public String writeGearNotesJsonString(ArrayList<GearNote> gearNotes) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(Constants.SPACE);
        writeGearNotesArrayList(jsonWriter, gearNotes);
        jsonWriter.close();
        return stringWriter.toString();
    }

    // write all gear notes to json string
    public void writeGearNotesArrayList(JsonWriter writer, ArrayList<GearNote> gearNotes) throws IOException {
        writer.beginArray();
        for (GearNote gearNote : gearNotes) {
            writeGearNote(writer, gearNote);
        }
        writer.endArray();
    }

    // write a gear note to json string
    public void writeGearNote(JsonWriter writer, GearNote gearNote) throws IOException {
        writer.beginObject();
        writer.name(Constants.JSON_TEXT).value(gearNote.getGearList());
        writer.endObject();
    }

    // read a json string containing a list of gear notes
    public ArrayList<GearNote> readGearNotesJsonString(String jsonString) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new StringReader(jsonString))) {
            return readGearNotesArrayList(jsonReader);
        }
    }

    // read a list of gear notes from a json string
    public ArrayList<GearNote> readGearNotesArrayList(JsonReader jsonReader) throws IOException {
        ArrayList<GearNote> gearNotes = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            gearNotes.add(readGearNote(jsonReader));
        }
        jsonReader.endArray();
        return gearNotes;
    }

    // read a gear note from a json string
    public GearNote readGearNote(JsonReader jsonReader) throws IOException {
        String gearList = Constants.EMPTY;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (Constants.JSON_TEXT.equals(name)) {
                gearList = jsonReader.nextString();
            } else {
                jsonReader.skipValue();
            }

        }
        jsonReader.endObject();
        return new GearNote(gearList);
    }

    // ### FLICKR NOTES ###

    // save flickr notes list on memory through a json string
    public void setFlickrNotesState(ArrayList<FlickrNote> notes) throws IOException {
        String jsonFlickrNotes = writeFlickrNotesJsonString(notes);
        savedState.edit().putString(Constants.FLICKR_NOTES, jsonFlickrNotes).apply();
    }

    // get flickr notes list from memory through a json string
    // if list was not saved yet creates a new array list
    public ArrayList<FlickrNote> getFlickrNotesState() throws IOException {
        String jsonFlickrNotes = savedState.getString(Constants.FLICKR_NOTES, Constants.EMPTY);
        if (Objects.requireNonNull(jsonFlickrNotes).equals(Constants.EMPTY)) {
            return new ArrayList<>();
        } else {
            return readFlickrNotesJsonString(jsonFlickrNotes);
        }
    }

    // get a json string of flickr notes list from memory
    public String getFlickrNotesJsonState() {
        return savedState.getString(Constants.FLICKR_NOTES, Constants.EMPTY);
    }

    // create a json string of a list of flickr note items
    public String writeFlickrNotesJsonString(ArrayList<FlickrNote> flickrNotes) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(Constants.SPACE);
        writeFlickrNotesArrayList(jsonWriter, flickrNotes);
        jsonWriter.close();
        return stringWriter.toString();
    }

    // write all flickr notes to json string
    public void writeFlickrNotesArrayList(JsonWriter writer, ArrayList<FlickrNote> flickrNotes) throws IOException {
        writer.beginArray();
        for (FlickrNote flickrNote : flickrNotes) {
            writeFlickrNote(writer, flickrNote);
        }
        writer.endArray();
    }

    // create a json string of a list of tags
    public String writeTagsJsonString(ArrayList<String> tags) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(Constants.SPACE);
        writeTagsArrayList(jsonWriter, tags);
        jsonWriter.close();
        return stringWriter.toString();
    }

    // write all flickr notes to json string
    public void writeTagsArrayList(JsonWriter writer, ArrayList<String> tags) throws IOException {
        writer.beginArray();
        for (String tag : tags) {
            writer.value(tag);
        }
        writer.endArray();
    }

    // write a flickr note to json string
    public void writeFlickrNote(JsonWriter writer, FlickrNote flickrNote) throws IOException {
        writer.beginObject();
        writer.name(Constants.JSON_TITLE).value(flickrNote.getTitle());
        writer.name(Constants.JSON_DESCRIPTION).value(flickrNote.getDescription());
        writer.name(Constants.JSON_TAGS).value(writeTagsJsonString(flickrNote.getTags()));
        writer.name(Constants.JSON_LATITUDE).value(flickrNote.getLatitude());
        writer.name(Constants.JSON_LONGITUDE).value(flickrNote.getLongitude());
        writer.name(Constants.JSON_START_TIME).value(flickrNote.getStartTime());
        writer.name(Constants.JSON_FINISH_TIME).value(flickrNote.getFinishTime());
        writer.name(Constants.JSON_SELECTED).value(flickrNote.isSelected());
        writer.endObject();
    }

    // read a json string containing a list of flickr notes
    public ArrayList<FlickrNote> readFlickrNotesJsonString(String jsonString) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new StringReader(jsonString))) {
            return readFlickrNotesArrayList(jsonReader);
        }
    }

    // read a list of flickr notes from a json string
    public ArrayList<FlickrNote> readFlickrNotesArrayList(JsonReader jsonReader) throws IOException {
        ArrayList<FlickrNote> flickrNotes = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            flickrNotes.add(readFlickrNote(jsonReader));
        }
        jsonReader.endArray();
        return flickrNotes;
    }

    // read a json string containing a list of flickr notes
    public ArrayList<String> readTagsJsonString(String jsonString) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new StringReader(jsonString))) {
            return readTagsArrayList(jsonReader);
        }
    }

    // read a list of flickr notes from a json string
    public ArrayList<String> readTagsArrayList(JsonReader jsonReader) throws IOException {
        ArrayList<String> tags = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            tags.add(jsonReader.nextString());
        }
        jsonReader.endArray();
        return tags;
    }

    // read a flickr note from a json string
    public FlickrNote readFlickrNote(JsonReader jsonReader) throws IOException {
        String title = Constants.EMPTY;
        String description = Constants.EMPTY;
        ArrayList<String> tags = new ArrayList<>();
        double latitude = Constants.DEFAULT_LATITUDE;
        double longitude = Constants.DEFAULT_LONGITUDE;
        String noteStartTime = Constants.EMPTY;
        String noteFinishTime = Constants.EMPTY;
        boolean noteSelected = false;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case Constants.JSON_TITLE:
                    title = jsonReader.nextString();
                    break;
                case Constants.JSON_DESCRIPTION:
                    description = jsonReader.nextString();
                    break;
                case Constants.JSON_TAGS:
                    tags = readTagsJsonString(jsonReader.nextString());
                    break;
                case Constants.JSON_LATITUDE:
                    latitude = jsonReader.nextDouble();
                    break;
                case Constants.JSON_LONGITUDE:
                    longitude = jsonReader.nextDouble();
                    break;
                case Constants.JSON_START_TIME:
                    noteStartTime = jsonReader.nextString();
                    break;
                case Constants.JSON_FINISH_TIME:
                    noteFinishTime = jsonReader.nextString();
                    break;
                case Constants.JSON_SELECTED:
                    noteSelected = jsonReader.nextBoolean();
                    break;
                default:
                    jsonReader.skipValue();
            }

        }
        jsonReader.endObject();
        FlickrNote flickrNote = new FlickrNote(title, description, tags, latitude, longitude, noteStartTime, noteFinishTime);
        flickrNote.setSelected(noteSelected);
        return flickrNote;
    }

    // ### GEAR LIST ###

    // save gear list on memory through a json string
    public void setGearListState(ArrayList<Gear> gearItem) throws IOException {
        String jsonGearNotes = writeGearListJsonString(gearItem);
        savedState.edit().putString(Constants.GEAR_LIST, jsonGearNotes).apply();
    }

    // get gears list from memory through a json string
    // if list was not saved yet creates a new array list
    public ArrayList<Gear> getGearListState() throws IOException {
        String jsonGearList = savedState.getString(Constants.GEAR_LIST, Constants.EMPTY);
        if (Objects.requireNonNull(jsonGearList).equals(Constants.EMPTY)) {
            return new ArrayList<>();
        } else {
            return readGearListJsonString(jsonGearList);
        }
    }

    // get a json string of gear list from memory
    public String getGearListJsonState() {
        return savedState.getString(Constants.GEAR_LIST, Constants.EMPTY);
    }

    // create a json string of a list of gear items
    public String writeGearListJsonString(ArrayList<Gear> gearList) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent(Constants.SPACE);
        writeGearArrayList(jsonWriter, gearList);
        jsonWriter.close();
        return stringWriter.toString();
    }

    // write all gear to json string
    public void writeGearArrayList(JsonWriter writer, ArrayList<Gear> gearList) throws IOException {
        writer.beginArray();
        for (Gear gearItem : gearList) {
            writeGear(writer, gearItem);
        }
        writer.endArray();
    }

    // write a gear to json string
    public void writeGear(JsonWriter writer, Gear gearItem) throws IOException {
        writer.beginObject();
        writer.name(Constants.JSON_GEAR_ITEM).value(gearItem.getGearItem());
        switch (dataType) {
            case Constants.GEAR_LIST_SAVED_STATE:
                writer.name(Constants.JSON_SELECTED).value(false);
                break;
            case Constants.GEAR_LIST_SELECTED_STATE:
                writer.name(Constants.JSON_SELECTED).value(gearItem.isSelected());
                break;
        }
        writer.endObject();
    }

    // read a json string containing a list of gear
    public ArrayList<Gear> readGearListJsonString(String jsonString) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new StringReader(jsonString))) {
            return readGearArrayList(jsonReader);
        }
    }

    // read a list of gear from a json string
    public ArrayList<Gear> readGearArrayList(JsonReader jsonReader) throws IOException {
        ArrayList<Gear> gearItem = new ArrayList<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            gearItem.add(readGearItem(jsonReader));
        }
        jsonReader.endArray();
        return gearItem;
    }

    // read a gear item
    public Gear readGearItem(JsonReader jsonReader) throws IOException {
        String gear = Constants.EMPTY;
        boolean selected = false;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case Constants.JSON_GEAR_ITEM:
                    gear = jsonReader.nextString();
                    break;
                case Constants.JSON_SELECTED:
                    selected = jsonReader.nextBoolean();
                    break;
                default:
                    jsonReader.skipValue();
            }

        }
        jsonReader.endObject();
        return new Gear(gear, selected);
    }


}
