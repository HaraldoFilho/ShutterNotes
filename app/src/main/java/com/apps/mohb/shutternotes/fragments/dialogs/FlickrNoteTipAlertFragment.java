/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrNoteTipAlertFragment.java
 *  Last modified : 6/7/24, 5:59 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.R;


public class FlickrNoteTipAlertFragment extends DialogFragment {

    public interface FlickrNoteTipDialogListener {
        void onFlickrNoteTipDialogPositiveClick(DialogFragment dialog);
    }

    private FlickrNoteTipDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.dialog_warning_title).setMessage(R.string.dialog_warning_flickr_note_message)
                .setPositiveButton(R.string.dialog_warning_button_ok, (dialog, id) -> mListener.onFlickrNoteTipDialogPositiveClick(FlickrNoteTipAlertFragment.this));

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the FlickrNoteTipDialogListener so we can send events to the host
            mListener = (FlickrNoteTipDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement FlickrNoteTipDialogListener");
        }
    }


}
