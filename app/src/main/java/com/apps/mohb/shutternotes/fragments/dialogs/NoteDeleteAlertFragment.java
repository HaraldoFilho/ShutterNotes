/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : NoteDeleteAlertFragment.java
 *  Last modified : 6/26/24, 10:14 AM
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


public class NoteDeleteAlertFragment extends DialogFragment {

    public interface NoteDeleteDialogListener {
        void onNoteDeleteDialogPositiveClick();

        void onNoteDeleteDialogNegativeClick(DialogFragment dialog);
    }

    private NoteDeleteDialogListener mListener;

    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_delete_note).setMessage(R.string.alert_message_no_undone)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onNoteDeleteDialogPositiveClick())
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onNoteDeleteDialogNegativeClick(NoteDeleteAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NetworkDeleteDialogListener so we can send events to the host
            mListener = (NoteDeleteDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context
                    + " must implement NoteDeleteDialogListener");
        }
    }

}