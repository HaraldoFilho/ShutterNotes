/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : ArchiveActivity.java
 *  Last modified : 6/13/24, 5:37 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.adapters.FlickrNotesListAdapter;
import com.apps.mohb.shutternotes.adapters.GearNotesListAdapter;
import com.apps.mohb.shutternotes.adapters.SimpleNotesListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.DeleteAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.NoteDeleteAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.RestoreAllNotesAlertFragment;
import com.apps.mohb.shutternotes.lists.Archive;
import com.apps.mohb.shutternotes.lists.Notebook;
import com.apps.mohb.shutternotes.views.GridViewWithHeaderAndFooter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;


public class ArchiveActivity extends AppCompatActivity implements
        NoteDeleteAlertFragment.NoteDeleteDialogListener,
        DeleteAllNotesAlertFragment.DeleteAllNotesAlertDialogListener,
        RestoreAllNotesAlertFragment.RestoreAllNotesAlertDialogListener {

    private Archive archive;
    private Notebook notebook;

    private BottomNavigationView botNavView;
    private GridViewWithHeaderAndFooter notesListGridView;

    private SimpleNotesListAdapter simpleNotesAdapter;
    private GearNotesListAdapter gearNotesAdapter;
    private FlickrNotesListAdapter flickrNotesAdapter;

    private AdapterView.AdapterContextMenuInfo menuInfo;

    Toast allNotesRestored;


    private final NavigationBarView.OnItemSelectedListener mOnItemSelectedListener
            = new NavigationBarView.OnItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bot_nav_simple:
                    notesListGridView.setAdapter(simpleNotesAdapter);
                    return true;
                case R.id.bot_nav_gear:
                    notesListGridView.setAdapter(gearNotesAdapter);
                    return true;
                case R.id.bot_nav_flickr:
                    notesListGridView.setAdapter(flickrNotesAdapter);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        botNavView = findViewById(R.id.botNavView);
        botNavView.setOnItemSelectedListener(mOnItemSelectedListener);

        // Create list header and footer, that will insert spaces on top and bottom of the
        // list to make material design effect elevation and shadow
        View listHeader = getLayoutInflater().inflate(R.layout.list_header, notesListGridView);
        View listFooter = getLayoutInflater().inflate(R.layout.archive_footer, notesListGridView);

        notesListGridView = findViewById(R.id.archivedNotesList);
        registerForContextMenu(notesListGridView);

        notesListGridView.addHeaderView(listHeader);
        notesListGridView.addFooterView(listFooter);
        listHeader.setClickable(false);
        listFooter.setClickable(false);

        // Define form factor of notes items accorging to screen height in pixels
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int listItemHeight = (int) (metrics.heightPixels / Constants.LIST_ITEM_HEIGHT_FACTOR);

        archive = Archive.getInstance(getApplicationContext());
        notebook = Notebook.getInstance(getApplicationContext());

        simpleNotesAdapter = new SimpleNotesListAdapter(getApplicationContext(), archive.getSimpleNotes(), listItemHeight);
        gearNotesAdapter = new GearNotesListAdapter(getApplicationContext(), archive.getGearNotes(), listItemHeight);
        flickrNotesAdapter = new FlickrNotesListAdapter(getApplicationContext(), archive.getFlickrNotes(), listItemHeight);
        notesListGridView.setAdapter(simpleNotesAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            archive.saveState();
            notebook.saveState();
        } catch (IOException e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
        simpleNotesAdapter = null;
        gearNotesAdapter = null;
        flickrNotesAdapter = null;
    }


    // CONTEXT MENU

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_archived, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            // Restore
            case R.id.restore:
                restoreNote(menuInfo.position);
                setAdapter(notesListGridView);
                return true;

            // Delete
            case R.id.delete:
                NoteDeleteAlertFragment dialogDelete = new NoteDeleteAlertFragment();
                dialogDelete.show(getSupportFragmentManager(), "NoteDeleteDialogFragment");
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_archive, menu);
        MenuItem menuItemDeleteAll = menu.findItem(R.id.action_delete_all);
        MenuItem menuItemRestoreAll = menu.findItem(R.id.action_archive_all);
        menuItemRestoreAll.setTitle(R.string.action_restore_all);
        menuItemRestoreAll.setIcon(R.drawable.ic_unarchive_white_24dp);
        menuItemDeleteAll.setEnabled(true);
        menuItemRestoreAll.setEnabled(true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Delete all notes
            case R.id.action_delete_all: {
                if (isCurrentListNotEmpty()) {
                    DialogFragment dialog = new DeleteAllNotesAlertFragment();
                    dialog.show(getSupportFragmentManager(), "DeleteAllNotesAlertFragment");
                }
                break;
            }

            // Restore all notes
            case R.id.action_archive_all: {
                if (isCurrentListNotEmpty()) {
                    DialogFragment dialog = new RestoreAllNotesAlertFragment();
                    dialog.show(getSupportFragmentManager(), "RestoreAllNotesAlertFragment");
                }
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNoteDeleteDialogPositiveClick(DialogFragment dialog) {
        removeNote(getCorrectPosition(menuInfo.position));
        setAdapter(notesListGridView);
    }

    @Override
    public void onNoteDeleteDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDeleteAllNotesDialogPositiveClick(DialogFragment dialog) {
        deleteAllNotes();
        setAdapter(notesListGridView);
    }


    @Override
    public void onDeleteAllNotesDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onRestoreAllNotesDialogPositiveClick(DialogFragment dialog) {
        restoreAllNotes();
        setAdapter(notesListGridView);
    }

    @Override
    public void onRestoreAllNotesDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            allNotesRestored.cancel();
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    // CLASS METHODS

    /*
         Set the correct adapter
     */
    @SuppressLint("NonConstantResourceId")
    private void setAdapter(GridViewWithHeaderAndFooter notesListGridView) {
        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                notesListGridView.setAdapter(simpleNotesAdapter);
                break;

            case R.id.bot_nav_gear:
                notesListGridView.setAdapter(gearNotesAdapter);
                break;

            case R.id.bot_nav_flickr:
                notesListGridView.setAdapter(flickrNotesAdapter);
                break;

        }
    }

    /*
         Remove note from the correct type
     */
    @SuppressLint("NonConstantResourceId")
    private void removeNote(int position) {
        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                archive.removeSimpleNote(position);
                break;

            case R.id.bot_nav_gear:
                archive.removeGearNote(position);
                break;

            case R.id.bot_nav_flickr:
                archive.removeFlickrNote(position);
                break;
        }
    }

    /*
         Restore note from the correct type
     */
    @SuppressLint("NonConstantResourceId")
    private void restoreNote(int position) {
        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                notebook.addNote(archive.getSimpleNotes().get(getCorrectPosition(position)));
                archive.removeSimpleNote(getCorrectPosition(position));
                break;

            case R.id.bot_nav_gear:
                notebook.addNote(archive.getGearNotes().get(getCorrectPosition(position)));
                archive.removeGearNote(getCorrectPosition(position));
                break;

            case R.id.bot_nav_flickr:
                notebook.addNote(archive.getFlickrNotes().get(getCorrectPosition(position)));
                archive.removeFlickrNote(getCorrectPosition(position));
                break;

        }
    }

    /*
         Delete all notes from the correct type
     */
    @SuppressLint("NonConstantResourceId")
    private void deleteAllNotes() {
        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                archive.getSimpleNotes().clear();
                break;

            case R.id.bot_nav_gear:
                archive.getGearNotes().clear();
                break;

            case R.id.bot_nav_flickr:
                archive.getFlickrNotes().clear();
                break;

        }
    }

    /*
         Restore all notes
    */
    @SuppressLint("NonConstantResourceId")
    private void restoreAllNotes() {

        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                for (int i = archive.getSimpleNotes().size() - 1; i >= 0; i--) {
                    notebook.addNote(archive.getSimpleNotes().get(i));
                    archive.getSimpleNotes().remove(i);
                }
                break;

            case R.id.bot_nav_gear:
                for (int i = archive.getGearNotes().size() - 1; i >= 0; i--) {
                    notebook.addNote(archive.getGearNotes().get(i));
                    archive.getGearNotes().remove(i);
                }
                break;

            case R.id.bot_nav_flickr:
                for (int i = archive.getFlickrNotes().size() - 1; i >= 0; i--) {
                    notebook.addNote(archive.getFlickrNotes().get(i));
                    archive.getFlickrNotes().remove(i);
                }
                break;

        }

        allNotesRestored = Toast.makeText((this), R.string.toast_all_notes_restored, Toast.LENGTH_SHORT);
        allNotesRestored.show();
    }

    /*
         Set the correct menu item status
     */
    @SuppressLint("NonConstantResourceId")
    private boolean isCurrentListNotEmpty() {

        switch (botNavView.getSelectedItemId()) {

            case R.id.bot_nav_simple:
                if (archive.getSimpleNotes().isEmpty()) {
                    return false;
                }
                break;

            case R.id.bot_nav_gear:
                if (archive.getGearNotes().isEmpty()) {
                    return false;
                }
                break;

            case R.id.bot_nav_flickr:
                if (archive.getFlickrNotes().isEmpty()) {
                    return false;
                }
                break;

        }

        return true;

    }

    /*
         List item position correction due to header
    */
    private int getCorrectPosition(int position) {
        return position - Constants.GRID_HEADER_POSITION;
    }

}

