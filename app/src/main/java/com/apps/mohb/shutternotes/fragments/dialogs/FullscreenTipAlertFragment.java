/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FullscreenTipAlertFragment.java
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


public class FullscreenTipAlertFragment extends DialogFragment {

    public interface FullscreenTipDialogListener {
        void onFullscreenTipDialogPositiveClick();
    }

    private FullscreenTipDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.dialog_instruction_title).setMessage(R.string.dialog_instruction_fullscreen_message)
                .setPositiveButton(R.string.dialog_warning_button_ok, (dialog, id) -> mListener.onFullscreenTipDialogPositiveClick());

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the FullscreenTipDialogListener so we can send events to the host
            mListener = (FullscreenTipDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context
                    + " must implement FullscreenTipDialogListener");
        }
    }


}
