/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : DeleteAllAlertFragment.java
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


public class DeleteAllAlertFragment extends DialogFragment {

    public interface DeleteAllAlertDialogListener {
        void onDeleteAllDialogPositiveClick();

        void onDeleteAllDialogNegativeClick(DialogFragment dialog);
    }

    private DeleteAllAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_delete_all_items).setMessage(R.string.alert_message_no_undone)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onDeleteAllDialogPositiveClick())
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onDeleteAllDialogNegativeClick(DeleteAllAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the NotesListClearDialogListener so we can send events to the host
            mListener = (DeleteAllAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context
                    + " must implement DeleteAllDialogListener");
        }
    }

}
