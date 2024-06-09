/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : SettingsActivity.java
 *  Last modified : 6/7/24, 5:59 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.apps.mohb.shutternotes.fragments.dialogs.PreferencesResetAlertFragment;

import java.util.Objects;


/*
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity implements
        PreferencesResetAlertFragment.PreferencesResetDialogListener {

    /*
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= Constants.LIST_HEAD
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }

        return true;

    };


    /*
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), Constants.EMPTY));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        // Create settings fragment which actually contain the settings screen
        GeneralPreferenceFragment preferenceFragment = new GeneralPreferenceFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, preferenceFragment)
                .commit();
        PreferenceManager.getDefaultSharedPreferences(this);

    }

    /*
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_defaults) {
            // Reset to defaults
            DialogFragment alertDialog = new PreferencesResetAlertFragment();
            alertDialog.show(getSupportFragmentManager(), "PreferencesResetAlertFragment");
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setHasOptionsMenu(true);

            Context context = getPreferenceManager().getContext();
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);

            // Font size
            ListPreference fontSizePreference = new ListPreference(context);
            fontSizePreference.setKey(getString(R.string.pref_key_font_size));
            fontSizePreference.setTitle(R.string.pref_title_font_size);
            fontSizePreference.setDefaultValue(getString(R.string.pref_def_font_size));
            fontSizePreference.setEntries(R.array.pref_font_size_titles);
            fontSizePreference.setEntryValues(R.array.pref_font_size_entry_values);

            // GENERAL SETTINGS
            PreferenceCategory generalSettingsCategory = new PreferenceCategory(context);
            generalSettingsCategory.setKey(getString(R.string.pref_group_general_settings_key));
            generalSettingsCategory.setTitle(R.string.pref_group_general_settings);
            preferenceScreen.addPreference(generalSettingsCategory);
            generalSettingsCategory.addPreference(fontSizePreference);

            // Notification sound
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Preference notificationSoundPreference = new Preference(context);
                notificationSoundPreference.setTitle(R.string.pref_title_notif_sound);
                notificationSoundPreference.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL);
                    startActivity(intent);
                    return false;
                });

                generalSettingsCategory.addPreference(notificationSoundPreference);

            } else {

                Preference notificationSoundPreference = new Preference(context);
                notificationSoundPreference.setTitle(R.string.pref_title_notif_sound);
                notificationSoundPreference.setOnPreferenceClickListener(preference -> {

                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

                    String existingValue = getRingtonePreferenceValue(getContext()); // TODO
                    if (existingValue != null) {
                        if (existingValue.isEmpty()) {
                            // Select "Silent"
                            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                        } else {
                            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                        }
                    } else {
                        // No ringtone has been selected, set to the default
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                    }

                    assert getActivity() != null;
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_RINGTONE);
                    return false;

                });

                generalSettingsCategory.addPreference(notificationSoundPreference);

            }

            // What to show on Fullscreen
            ListPreference whatShowPreference = new ListPreference(context);
            whatShowPreference.setKey(getString(R.string.pref_key_what_show));
            whatShowPreference.setTitle(R.string.pref_title_what_show);
            whatShowPreference.setDefaultValue(getString(R.string.pref_def_what_show));
            whatShowPreference.setEntries(R.array.pref_what_show_titles);
            whatShowPreference.setEntryValues(R.array.pref_what_show_entry_values);

            // Map zoom level
            ListPreference mapZoomLevelPreference = new ListPreference(context);
            mapZoomLevelPreference.setKey(getString(R.string.pref_key_map_zoom_level));
            mapZoomLevelPreference.setTitle(R.string.pref_title_map_zoom_level);
            mapZoomLevelPreference.setDefaultValue(getString(R.string.pref_def_map_zoom_level));
            mapZoomLevelPreference.setEntries(R.array.pref_map_zoom_level_titles);
            mapZoomLevelPreference.setEntryValues(R.array.pref_map_zoom_level_entry_values);

            // FLICKR NOTES
            PreferenceCategory flickrNotesCategory = new PreferenceCategory(context);
            flickrNotesCategory.setKey(getString(R.string.pref_group_flickr_notes_settings_key));
            flickrNotesCategory.setTitle(R.string.pref_group_flickr_notes);
            preferenceScreen.addPreference(flickrNotesCategory);
            flickrNotesCategory.addPreference(whatShowPreference);
            flickrNotesCategory.addPreference(mapZoomLevelPreference);

            // Archive notes
            SwitchPreferenceCompat archiveNotesPreference = new SwitchPreferenceCompat(context);
            archiveNotesPreference.setKey(getString(R.string.pref_key_archive_notes));
            archiveNotesPreference.setTitle(getString(R.string.pref_title_archive_notes));
            archiveNotesPreference.setSummaryOn(R.string.pref_archive_notes_summ_on);
            archiveNotesPreference.setSummaryOff(R.string.pref_archive_notes_summ_off);
            archiveNotesPreference.setDefaultValue(Boolean.valueOf(getString(R.string.pref_def_archive_notes)));

            // Overwrite data
            SwitchPreferenceCompat overwriteDataPreference = new SwitchPreferenceCompat(context);
            overwriteDataPreference.setKey(getString(R.string.pref_key_overwrite_data));
            overwriteDataPreference.setTitle(getString(R.string.pref_title_overwrite_data));
            overwriteDataPreference.setSummaryOn(R.string.pref_overwrite_data_summ_on);
            overwriteDataPreference.setSummaryOff(R.string.pref_overwrite_data_summ_off);
            overwriteDataPreference.setDefaultValue(Boolean.valueOf(getString(R.string.pref_def_overwrite_data)));

            // Upload location
            SwitchPreferenceCompat uploadLocationPreference = new SwitchPreferenceCompat(context);
            uploadLocationPreference.setKey(getString(R.string.pref_key_upload_location));
            uploadLocationPreference.setTitle(getString(R.string.pref_title_upload_location));
            uploadLocationPreference.setSummaryOn(R.string.pref_upload_location_summ_on);
            uploadLocationPreference.setSummaryOff(R.string.pref_upload_location_summ_off);
            uploadLocationPreference.setDefaultValue(Boolean.valueOf(getString(R.string.pref_def_upload_location)));

            // Upload tags
            SwitchPreferenceCompat uploadTagsPreference = new SwitchPreferenceCompat(context);
            uploadTagsPreference.setKey(getString(R.string.pref_key_upload_tags));
            uploadTagsPreference.setTitle(getString(R.string.pref_title_upload_tags));
            uploadTagsPreference.setSummaryOn(R.string.pref_upload_tags_summ_on);
            uploadTagsPreference.setSummaryOff(R.string.pref_upload_tags_summ_off);
            uploadTagsPreference.setDefaultValue(Boolean.valueOf(getString(R.string.pref_def_upload_tags)));

            // Overwrite tags
            ListPreference overwriteTagsPreference = new ListPreference(context);
            overwriteTagsPreference.setKey(getString(R.string.pref_key_overwrite_tags));
            overwriteTagsPreference.setTitle(R.string.pref_title_overwrite_tags);
            overwriteTagsPreference.setDefaultValue(getString(R.string.pref_def_overwrite_tags));
            overwriteTagsPreference.setEntries(R.array.pref_overwrite_tags_titles);
            overwriteTagsPreference.setEntryValues(R.array.pref_overwrite_tags_entry_values);

            // UPLOAD
            PreferenceCategory uploadCategory = new PreferenceCategory(context);
            uploadCategory.setKey(getString(R.string.pref_group_flickr_upload_settings_key));
            uploadCategory.setTitle(R.string.pref_group_flickr_upload);
            preferenceScreen.addPreference(uploadCategory);
            uploadCategory.addPreference(archiveNotesPreference);
            uploadCategory.addPreference(overwriteDataPreference);
            uploadCategory.addPreference(uploadLocationPreference);
            uploadCategory.addPreference(uploadTagsPreference);
            uploadCategory.addPreference(overwriteTagsPreference);

            setPreferenceScreen(preferenceScreen);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_KEY_FONT_SIZE)));
            bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_KEY_WHAT_SHOW)));
            bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_KEY_MAP_ZOOM_LEVEL)));
            bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_KEY_OVERWRITE_TAGS)));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_RINGTONE && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                setRingtonPreferenceValue(ringtone.toString(), this); // TODO
            } else {
                // "Silent" was selected
                setRingtonPreferenceValue("", this); // TODO
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static void setRingtonPreferenceValue(String toString, Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        defaultSharedPreferences.edit().putString(Constants.PREF_KEY_NOTIF_SOUND, toString).apply();
    }

    private static String getRingtonePreferenceValue(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(Constants.PREF_KEY_NOTIF_SOUND, Constants.PREF_DEF_NOTIF_SOUND);
    }

    // RESET TO DEFAULTS DIALOG

    @Override // Yes
    public void onAlertDialogPositiveClick(DialogFragment dialog) {
        // Clear settings on memory
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();

        // Update settings screen with the default values
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    @Override // No
    public void onAlertDialogNegativeClick(DialogFragment dialog) {
        Objects.requireNonNull(dialog.getDialog()).cancel();
    }

}
