/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : GearNoteActivity.java
 *  Last modified : 6/26/24, 2:36 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.adapters.GearNoteAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.DeleteAllAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.EditGearListDialogFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.GearDeleteAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.RestoreBackupAlertFragment;
import com.apps.mohb.shutternotes.lists.GearList;

import java.io.IOException;
import java.util.Objects;


public class GearNoteActivity extends AppCompatActivity
        implements EditGearListDialogFragment.EditGearListDialogListener,
        GearDeleteAlertFragment.GearDeleteDialogListener,
        DeleteAllAlertFragment.DeleteAllAlertDialogListener,
        RestoreBackupAlertFragment.RestoreBackupAlertDialogListener {

    private GearList gearList;
    private ListView gearListView;
    private GearNoteAdapter gearNoteAdapter;

    private AdapterView.AdapterContextMenuInfo menuInfo;

    private int callerActivity;

    private Toast mustPickup;
    private Toast reorderedItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gear_note);

        View listHeader = getLayoutInflater().inflate(R.layout.list_header, gearListView);
        View listFooter = getLayoutInflater().inflate(R.layout.list_footer, gearListView);

        Button buttonCancel = findViewById(R.id.buttonGearNoteCancel);
        Button buttonReset = findViewById(R.id.buttonGearNoteReset);
        Button buttonOK = findViewById(R.id.buttonGearNoteOk);

        gearListView = findViewById(R.id.gearList);

        gearListView.addHeaderView(listHeader);
        gearListView.addFooterView(listFooter);
        listHeader.setClickable(false);
        listFooter.setClickable(false);

        gearList = GearList.getInstance();

        gearListView.setOnItemClickListener((adapterView, view, i, l) -> {

            boolean itemIsSelected;

            try {
                itemIsSelected = gearList.get(getCorrectPosition(i)).isSelected();
            } catch (Exception e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                return;
            }

            if (!itemIsSelected) {
                gearList.get(getCorrectPosition(i)).setSelected(true);
                gearList.moveToBottomOfLastSelected(getCorrectPosition(i));
            } else {
                gearList.get(getCorrectPosition(i)).setSelected(false);
                gearList.moveToBottom(getCorrectPosition(i));
            }
            gearListView.invalidateViews();
        });

        // menu shown when a list item is long clicked
        registerForContextMenu(gearListView);

        callerActivity = Objects.requireNonNull(getIntent().getExtras()).getInt(Constants.KEY_CALLER_ACTIVITY);

        try {
            switch (callerActivity) {
                case Constants.ACTIVITY_GEAR_NOTE:
                    Objects.requireNonNull(gearList).loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                    break;
                case Constants.ACTIVITY_FLICKR_NOTE:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.activity_title_add_tags);
                    buttonOK.setText(R.string.button_ok);
                    Objects.requireNonNull(gearList).loadState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
                    if (gearList.getList().isEmpty()) {
                        gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                    }
                    break;
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        buttonCancel.setOnClickListener(view -> finish());

        buttonReset.setOnClickListener(view -> {
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }
            gearNoteAdapter = null;
            gearNoteAdapter = new GearNoteAdapter(this, gearList.getList());
            gearListView.setAdapter(gearNoteAdapter);
        });

        buttonOK.setOnClickListener(view -> {

            switch (callerActivity) {
                case Constants.ACTIVITY_GEAR_NOTE:
                    String textString = gearList.getGearListText();
                    if (textString.equals(Constants.EMPTY)) {
                        mustPickup = Toast.makeText((this), R.string.toast_must_pickup, Toast.LENGTH_SHORT);
                        mustPickup.show();
                    } else {
                        Intent intent = new Intent(this, FullscreenNoteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_FULL_SCREEN_TEXT, textString.trim());
                        bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_GEAR_NOTE);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    break;

                case Constants.ACTIVITY_FLICKR_NOTE:
                    try {
                        gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
                    } catch (IOException e) {
                        Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                    }
                    cancelAllToasts();
                    finish();
                    break;

            }
        });

        gearNoteAdapter = new GearNoteAdapter(this, gearList.getList());

        // create notes list
        gearListView.setAdapter(gearNoteAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelAllToasts();
    }

    private void cancelAllToasts() {
        try {
            mustPickup.cancel();
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
        try {
            reorderedItems.cancel();
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    // CONTEXT MENU

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_gear, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            // Edit
            case R.id.edit:
                gearList.setEditedGearItemText(getApplicationContext(), gearList.getGearItem(getCorrectPosition(menuInfo.position)));
                gearList.setEditedGearItemPosition(getApplicationContext(), getCorrectPosition(menuInfo.position));
                DialogFragment addGearDialog = new EditGearListDialogFragment();
                addGearDialog.show(getSupportFragmentManager(), "AddGearDialogFragment");
                return true;

            // Delete
            case R.id.delete:
                GearDeleteAlertFragment dialogDelete = new GearDeleteAlertFragment();
                dialogDelete.show(getSupportFragmentManager(), "GearDeleteDialogFragment");
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_gear_note, menu);
        menu.findItem(R.id.action_add_gear).setEnabled(true);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (!gearList.getList().isEmpty()) {
            menu.findItem(R.id.action_select_all).setEnabled(true);
            menu.findItem(R.id.action_delete_all).setEnabled(true);
            menu.findItem(R.id.action_reorder).setEnabled(true);
            menu.findItem(R.id.action_export_list).setEnabled(true);
        } else {
            menu.findItem(R.id.action_select_all).setEnabled(false);
            menu.findItem(R.id.action_delete_all).setEnabled(false);
            menu.findItem(R.id.action_reorder).setEnabled(false);
            menu.findItem(R.id.action_export_list).setEnabled(false);
        }
        return super.onMenuOpened(featureId, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Add gear
            case R.id.action_add_gear: {
                gearList.setEditedGearItemText(getApplicationContext(), Constants.EMPTY);
                gearList.setEditedGearItemPosition(getApplicationContext(), Constants.NULL_POSITION);
                DialogFragment addGearDialog = new EditGearListDialogFragment();
                addGearDialog.show(getSupportFragmentManager(), "AddGearDialogFragment");
                break;
            }

            // Select all
            case R.id.action_select_all: {
                for (int i = 0; i < gearList.size(); i++) {
                    gearList.get(i).setSelected(true);
                }
                gearListView.invalidateViews();
                break;
            }

            // Delete all
            case R.id.action_delete_all: {
                DialogFragment dialog = new DeleteAllAlertFragment();
                dialog.show(getSupportFragmentManager(), "DeleteAllAlertFragment");
                break;
            }

            // Reorder
            case R.id.action_reorder: {
                try {
                    gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                } catch (IOException e) {
                    Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
                }
                reorderedItems = Toast.makeText((this), R.string.toast_reordered, Toast.LENGTH_SHORT);
                reorderedItems.show();
                break;
            }

            case R.id.action_export_list: {
                if (!gearList.getList().isEmpty()) {
                    createFile();
                } else {
                    Toast.makeText(this, R.string.toast_empty_list, Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.action_import_list: {
                DialogFragment restoreBackupDialog = new RestoreBackupAlertFragment();
                restoreBackupDialog.show(getSupportFragmentManager(), "RestoreBackupDialogFragment");
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditGearListDialogPositiveClick() {
        SharedPreferences gearTextEdit = this.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Context.MODE_PRIVATE);
        int itemPosition = gearTextEdit.getInt(Constants.GEAR_ITEM_POSITION, Constants.NULL_POSITION);
        if (itemPosition < 0) {
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }
        } else {
            String editedText = gearTextEdit.getString(Constants.GEAR_EDITED_TEXT, Constants.EMPTY);
            gearList.setGearItem(itemPosition, editedText);
        }
        gearNoteAdapter = null;
        gearNoteAdapter = new GearNoteAdapter(this, gearList.getList());
        gearListView.setAdapter(gearNoteAdapter);
        try {
            gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
            if (callerActivity == Constants.ACTIVITY_FLICKR_NOTE) {
                gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    public void onEditGearListDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onGearDeleteDialogPositiveClick() {
        gearList.remove(getCorrectPosition(menuInfo.position));
        try {
            gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
        gearListView.invalidateViews();
    }

    @Override
    public void onGearDeleteDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDeleteAllDialogPositiveClick() {
        gearList.getList().clear();
        gearListView.setAdapter(gearNoteAdapter);
        try {
            gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onDeleteAllDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onRestoreBackupDialogPositiveClick() {
        openFile();
    }

    @Override
    public void onRestoreBackupDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    // CLASS METHODS

    private int getCorrectPosition(int position) {
        return position - Constants.LIST_HEADER_POSITION;
    }


    // Request code for creating a txt document.

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        createFile.launch(intent);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        openFile.launch(intent);
    }

    final ActivityResultLauncher<Intent> createFile =
            registerForActivityResult(new
                            ActivityResultContracts.StartActivityForResult(),
                    (result) -> {
                        Intent resultData = result.getData();
                        try {
                            assert result.getData() != null;
                            gearList.backupList(getApplicationContext(), Uri.parse(String.valueOf(resultData.getData())));
                            Toast.makeText(getBaseContext(), R.string.toast_exported, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

    final ActivityResultLauncher<Intent> openFile =
            registerForActivityResult(new
                            ActivityResultContracts.StartActivityForResult(),
                    (result) -> {
                        Intent resultData = result.getData();
                        try {
                            gearList.getList().clear();
                            assert resultData != null;
                            gearList.restoreList(getApplicationContext(), Uri.parse(String.valueOf(resultData.getData())));
                            gearListView.setAdapter(gearNoteAdapter);
                            Toast.makeText(getBaseContext(), R.string.toast_imported, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
}
