/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : RunInBackgroundAlertFragment.java
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


public class RunInBackgroundAlertFragment extends DialogFragment {

    public interface RunInBackgroundAlertDialogListener {
        void onRunInBackgroundDialogPositiveClick(DialogFragment dialog);

        void onRunInBackgroundDialogNegativeClick(DialogFragment dialog);
    }

    private RunInBackgroundAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_run_background).setMessage(R.string.alert_message_run_background)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onRunInBackgroundDialogPositiveClick(RunInBackgroundAlertFragment.this))
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onRunInBackgroundDialogNegativeClick(RunInBackgroundAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the RunInBackgroundDialogListener so we can send events to the host
            mListener = (RunInBackgroundAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement RunInBackgroundDialogListener");
        }
    }

}
