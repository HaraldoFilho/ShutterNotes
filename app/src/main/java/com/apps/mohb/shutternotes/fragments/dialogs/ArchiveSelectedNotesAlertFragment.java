/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : ArchiveSelectedNotesAlertFragment.java
 *  Last modified : 6/8/24, 10:58 AM
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


public class ArchiveSelectedNotesAlertFragment extends DialogFragment {

    public interface ArchiveSelectedNotesAlertDialogListener {
        void onArchiveSelectedNotesDialogPositiveClick(DialogFragment dialog);

        void onArchiveSelectedNotesDialogNegativeClick(DialogFragment dialog);
    }

    private ArchiveSelectedNotesAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_archive_selected_notes).setMessage(R.string.alert_message_can_be_restored)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onArchiveSelectedNotesDialogPositiveClick(ArchiveSelectedNotesAlertFragment.this))
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onArchiveSelectedNotesDialogNegativeClick(ArchiveSelectedNotesAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ArchiveSelectedNotesDialogListener so we can send events to the host
            mListener = (ArchiveSelectedNotesAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ArchiveSelectedNotesDialogListener");
        }
    }

}
