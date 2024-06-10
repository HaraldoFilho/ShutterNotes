/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : ConfirmUploadAlertFragment.java
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


public class ConfirmUploadAlertFragment extends DialogFragment {

    public interface ConfirmUploadAlertDialogListener {
        void onConfirmUploadDialogPositiveClick(DialogFragment dialog);

        void onConfirmUploadDialogNegativeClick(DialogFragment dialog);
    }

    private ConfirmUploadAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_confirm_upload).setMessage(R.string.alert_message_confirm_upload)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onConfirmUploadDialogPositiveClick(ConfirmUploadAlertFragment.this))
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onConfirmUploadDialogNegativeClick(ConfirmUploadAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the ConfirmUploadDialogListener so we can send events to the host
            mListener = (ConfirmUploadAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ConfirmUploadDialogListener");
        }
    }

}
