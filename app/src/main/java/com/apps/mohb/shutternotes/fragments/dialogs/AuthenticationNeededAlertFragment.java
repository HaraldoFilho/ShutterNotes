/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : AuthenticationNeededAlertFragment.java
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


public class AuthenticationNeededAlertFragment extends DialogFragment {

    public interface AuthenticationNeededAlertDialogListener {
        void onAuthenticationNeededDialogPositiveClick(DialogFragment dialog);

        void onAuthenticationNeededDialogNegativeClick(DialogFragment dialog);
    }

    private AuthenticationNeededAlertDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_authenticate).setMessage(R.string.alert_message_authenticate)
                .setPositiveButton(R.string.alert_button_yes, (dialog, id) -> mListener.onAuthenticationNeededDialogPositiveClick(AuthenticationNeededAlertFragment.this))
                .setNegativeButton(R.string.alert_button_no, (dialog, id) -> mListener.onAuthenticationNeededDialogNegativeClick(AuthenticationNeededAlertFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the AuthenticationNeededDialogListener so we can send events to the host
            mListener = (AuthenticationNeededAlertDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement AuthenticationNeededDialogListener");
        }
    }

}
