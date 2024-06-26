/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : PreferencesResetAlertFragment.java
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


public class PreferencesResetAlertFragment extends DialogFragment {

    public interface PreferencesResetDialogListener {
        void onAlertDialogPositiveClick();

        void onAlertDialogNegativeClick(DialogFragment dialog);
    }

    private PreferencesResetDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.alert_title_reset_preferences).setMessage(R.string.alert_message_reset_preferences)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onAlertDialogPositiveClick())
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onAlertDialogNegativeClick(PreferencesResetAlertFragment.this));

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PreferencesResetDialogListener so we can send events to the host
            mListener = (PreferencesResetDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context
                    + " must implement PreferencesResetDialogListener");
        }
    }

}
