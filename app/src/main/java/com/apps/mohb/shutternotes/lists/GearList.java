/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : GearList.java
 *  Last modified : 6/26/24, 10:14 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.lists;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.apps.mohb.shutternotes.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;


public class GearList {

    private static SavedState gearListSavedState;
    private static SavedState gearListSelectedState;
    private SharedPreferences gearTextEdit;

    private static GearList gearList;
    private static ArrayList<Gear> list;

    public GearList() {
        list = new ArrayList<>();
    }

    public static GearList getInstance() {
        if (gearList == null) {
            gearList = new GearList();
        }
        return gearList;
    }

    public void loadState(Context context, String dataType) throws IOException {
        switch (dataType) {
            case Constants.GEAR_LIST_SAVED_STATE:
                if (gearListSavedState == null) {
                    gearListSavedState = new SavedState(context, dataType);
                }
                list = gearListSavedState.getGearListState();
                break;
            case Constants.GEAR_LIST_SELECTED_STATE:
                if (gearListSelectedState == null) {
                    gearListSelectedState = new SavedState(context, dataType);
                }
                list = gearListSelectedState.getGearListState();
                break;
            default:
                break;
        }
    }

    public void saveState(Context context, String dataType) throws IOException {
        switch (dataType) {
            case Constants.GEAR_LIST_SAVED_STATE:
                if (gearListSavedState == null) {
                    gearListSavedState = new SavedState(context, dataType);
                }
                gearListSavedState.setGearListState(list);
                break;
            case Constants.GEAR_LIST_SELECTED_STATE:
                if (gearListSelectedState == null) {
                    gearListSelectedState = new SavedState(context, dataType);
                }
                gearListSelectedState.setGearListState(list);
                break;
            default:
                break;
        }
    }

    public ArrayList<Gear> getList() {
        return list;
    }

    public void add(String gear) {
        Gear gearItem = new Gear(gear);
        list.add(gearItem);
    }

    public void add(int position, String gear) {
        Gear gearItem = new Gear(gear);
        list.add(position, gearItem);
    }

    public Gear get(int position) {
        return list.get(position);
    }

    public String getGearItem(int position) {
        return list.get(position).getGearItem();
    }

    public void setGearItem(int position, String gear) {
        list.get(position).setGearItem(gear);
    }

    public void moveToBottom(int position) {
        Gear gear = list.get(position);
        list.remove(position);
        list.add(gear);
    }

    public void moveToBottomOfLastSelected(int position) {
        Gear gear = list.get(position);
        list.remove(position);
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isSelected()) {
                if (i < list.size() - 1) {
                    list.add(i + 1, gear);
                    return;
                } else {
                    list.add(gear);
                    return;
                }
            }
        }
        list.add(Constants.LIST_HEAD, gear);

    }

    public ArrayList<String> getFlickrTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                tags.add(get(i).getGearItem());
            }
        }
        return tags;
    }

    public String getGearListText() {
        String textString = Constants.EMPTY;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                textString = textString.concat(list.get(i).getGearItem() + Constants.NEW_LINE);
            }
        }
        return textString;
    }

    public void remove(int position) {
        list.remove(position);
    }

    public int size() {
        return list.size();
    }

    public String getEditedGearItemText(Context context) {
        gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Context.MODE_PRIVATE);
        return gearTextEdit.getString(Constants.GEAR_EDITED_TEXT, Constants.EMPTY);
    }

    public void setEditedGearItemText(Context context, String textString) {
        gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Context.MODE_PRIVATE);
        gearTextEdit.edit().putString(Constants.GEAR_EDITED_TEXT, textString).apply();
    }

    public int getEditedGearItemPosition(Context context) {
        gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Context.MODE_PRIVATE);
        return gearTextEdit.getInt(Constants.GEAR_ITEM_POSITION, Constants.NULL_POSITION);
    }

    public void setEditedGearItemPosition(Context context, int itemPosition) {
        gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Context.MODE_PRIVATE);
        gearTextEdit.edit().putInt(Constants.GEAR_ITEM_POSITION, itemPosition).apply();
    }


    // Backup and Restore methods

    public void backupList(Context context, Uri uri) throws IOException {
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri, "w");
            assert pfd != null;
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            for (int i = 0; i < list.size(); i++) {
                String item = list.get(i).getGearItem();
                Log.d(Constants.LOG_DEBUG_TAG, "Item: " + item);
                fileOutputStream.write(item.getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            Log.e(Constants.LOG_ERROR_TAG, Log.getStackTraceString(e));
        }
    }

    public void restoreList(Context context, Uri uri) throws IOException {

        String line;

        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri)) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                while ((line = reader.readLine()) != null) {
                    Log.d(Constants.LOG_DEBUG_TAG, "Line: ");
                    gearList.add(line.trim());
                }
            }
        }
        gearList.saveState(context, Constants.GEAR_LIST_SAVED_STATE);
    }

}

