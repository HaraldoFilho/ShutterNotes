/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrPhotosetsListAdapter.java
 *  Last modified : 6/13/24, 3:52 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;
import com.flickr4java.flickr.photosets.Photoset;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class FlickrPhotosetsListAdapter extends ArrayAdapter<Photoset> {


    public FlickrPhotosetsListAdapter(@NonNull Context context, Collection<Photoset> photosetsList) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, (List<Photoset>) photosetsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photoset_item, parent, false);
        }

        ImageView coverPhotoView = convertView.findViewById(R.id.imagePhotosetCover);
        TextView titleView = convertView.findViewById(R.id.textPhotosetTitle);
        TextView sizeView = convertView.findViewById(R.id.textPhotosetSize);

        Photoset photoset = getItem(position);
        String imgCoverPhotoUrl = Objects.requireNonNull(photoset).getPrimaryPhoto().getSquareLargeUrl();

        String txtTitle = photoset.getTitle();

        int size = photoset.getPhotoCount();
        String txtSize = String.valueOf(size).concat(Constants.SPACE)
                .concat(getContext().getResources().getString(R.string.text_photo));
        if (size > 1) {
            txtSize = txtSize + getContext().getResources().getString(R.string.text_s);
        }

        Picasso.get().load(imgCoverPhotoUrl).into(coverPhotoView);

        titleView.setText(txtTitle);
        titleView.setTypeface(Typeface.defaultFromStyle(android.graphics.Typeface.BOLD));
        sizeView.setText(txtSize);

        return convertView;

    }
}
