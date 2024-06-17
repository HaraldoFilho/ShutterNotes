/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : ArchiveAllNotesAlertFragment.java
 *  Last modified : 6/17/24, 9:46 AM
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


public class ArchiveAllNotesAlertFragment extends DialogFragment {

    public interface ArchiveAllNotesAlertDialogListener {
        void onArchiveAllNotesDialogPositiveClick(DialogFragment dialog);

        void onArchiveAllNotesDialogNegativeClick(DialogFragment dialog);
    }

    private ArchiveAllNotesAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_archive_all_notes).setMessage(R.string.alert_message_can_be_restored)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onArchiveAllNotesDialogPositiveClick(ArchiveAllNotesAlertFragment.this))
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onArchiveAllNotesDialogNegativeClick(ArchiveAllNotesAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ArchiveAllNotesDialogListener so we can send events to the host
            mListener = (ArchiveAllNotesAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ArchiveAllNotesDialogListener");
        }
    }

}
