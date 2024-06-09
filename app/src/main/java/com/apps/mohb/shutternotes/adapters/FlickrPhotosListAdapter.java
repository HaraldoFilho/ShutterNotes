/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrPhotosListAdapter.java
 *  Last modified : 6/7/24, 5:59 PM
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
import com.flickr4java.flickr.photos.Photo;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class FlickrPhotosListAdapter extends ArrayAdapter<Photo> {


    public FlickrPhotosListAdapter(@NonNull Context context, Collection<Photo> photosList) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, (List<Photo>) photosList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, parent, false);
        }

        ImageView photoView = convertView.findViewById(R.id.imagePhoto);
        TextView titleView = convertView.findViewById(R.id.textPhotoTitle);
        TextView descriptionView = convertView.findViewById(R.id.textPhotoDescription);

        Photo photo = getItem(position);
        String imgPhotoUrl = Objects.requireNonNull(photo).getSquareLargeUrl();

        String txtTitle = photo.getTitle();
        String txtDescription = photo.getDescription();

        Picasso.get().load(imgPhotoUrl).into(photoView);

        titleView.setText(txtTitle);
        titleView.setTypeface(Typeface.defaultFromStyle(android.graphics.Typeface.BOLD));
        descriptionView.setText(txtDescription);

        return convertView;

    }
}
