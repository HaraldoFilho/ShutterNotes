/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : GearNoteAdapter.java
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
import com.apps.mohb.shutternotes.lists.Gear;

import java.util.ArrayList;
import java.util.Objects;


public class GearNoteAdapter extends ArrayAdapter<Gear> {


    public GearNoteAdapter(@NonNull Context context, ArrayList<Gear> note) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, note);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Gear item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gear_item, parent, false);
        }

        TextView txtItem = convertView.findViewById(R.id.gearView);
        txtItem.setText(Objects.requireNonNull(item).getGearItem());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

        switch (Objects.requireNonNull(prefKey)) {

            case Constants.PREF_FONT_SIZE_SMALL:
                txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_SMALL_MEDIUM);
                break;

            case Constants.PREF_FONT_SIZE_MEDIUM:
                txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_MEDIUM_MEDIUM);
                break;

            case Constants.PREF_FONT_SIZE_LARGE:
                txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_LARGE_MEDIUM);
                break;

        }

        if (item.isSelected()) {
            convertView.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.gear_item_tile_selected, null));
            //txtItem.setTextColor(getContext().getResources().getColor(R.color.colorBlackText, null));
        } else {
            convertView.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.gear_item_tile, null));
            //txtItem.setTextColor(getContext().getResources().getColor(R.color.colorBlackText, null));
        }

        return convertView;

    }
}
