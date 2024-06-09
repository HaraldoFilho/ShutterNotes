/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : EditGearListDialogFragment.java
 *  Last modified : 6/8/24, 10:58 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;
import com.apps.mohb.shutternotes.lists.GearList;

import java.io.IOException;
import java.util.Objects;

public class EditGearListDialogFragment extends DialogFragment {

    public interface EditGearListDialogListener {
        void onEditGearListDialogPositiveClick(DialogFragment dialog);

        void onEditGearListDialogNegativeClick(DialogFragment dialog);
    }

    private EditGearListDialogListener mListener;
    private GearList gearList;
    private EditText text;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        try {
            gearList = GearList.getInstance();
            Objects.requireNonNull(gearList).loadState(requireActivity().getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_edit_gear_list_dialog, null);

        text = view.findViewById(R.id.txtEditGear);
        final String editedText = gearList.getEditedGearItemText(requireContext());
        final int itemPosition = gearList.getEditedGearItemPosition(requireContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.dialog_add_gear_title);

        if (!editedText.isEmpty()) {
            text.setText(editedText);
            builder.setTitle(R.string.dialog_edit_gear_title);
        }

        builder.setPositiveButton(R.string.button_ok, (dialog, id) -> {
                    String textString = text.getText().toString();
                    if (!textString.isEmpty()) {
                        if (editedText.isEmpty()) {
                            gearList.add(textString);
                        } else if (itemPosition != Constants.NULL_POSITION) {
                            gearList.setEditedGearItemText(requireContext(), textString);
                            gearList.setEditedGearItemPosition(requireContext(), itemPosition);
                        }
                        try {
                            gearList.saveState(requireActivity().getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                        } catch (IOException e) {
                            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                        }
                    }
                    mListener.onEditGearListDialogPositiveClick(EditGearListDialogFragment.this);
                })
                .setNegativeButton(R.string.button_cancel, (dialog, id) -> mListener.onEditGearListDialogNegativeClick(EditGearListDialogFragment.this));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the BookmarkEditDialogListener so we can send events to the host
            mListener = (EditGearListDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement AddGearDialogListener");
        }
    }

}
