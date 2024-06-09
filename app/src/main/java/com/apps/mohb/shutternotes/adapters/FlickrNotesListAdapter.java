/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrNotesListAdapter.java
 *  Last modified : 6/7/24, 8:32 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;
import com.apps.mohb.shutternotes.notes.FlickrNote;

import java.util.ArrayList;
import java.util.Objects;


public class FlickrNotesListAdapter extends ArrayAdapter<FlickrNote> {

    private final int itemHeight;

    public FlickrNotesListAdapter(@NonNull Context context, ArrayList<FlickrNote> notesList, int itemHeight) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, notesList);
        this.itemHeight = itemHeight;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        FlickrNote note = getItem(position);
        boolean selected = Objects.requireNonNull(note).isSelected();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        convertView.setMinimumHeight(itemHeight);

        if (selected) {
            convertView.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.flickr_item_tile_selected, null));
        } else {
            convertView.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.flickr_item_tile, null));
        }

        TextView txtTitle = convertView.findViewById(R.id.textView);
        txtTitle.setText(note.getTitle());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

        switch (Objects.requireNonNull(prefKey)) {

            case Constants.PREF_FONT_SIZE_SMALL:
                txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_SMALL_SMALL);
                break;

            case Constants.PREF_FONT_SIZE_MEDIUM:
                txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_MEDIUM_SMALL);
                break;

            case Constants.PREF_FONT_SIZE_LARGE:
                txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_LARGE_SMALL);
                break;

        }

        return convertView;

    }
}
