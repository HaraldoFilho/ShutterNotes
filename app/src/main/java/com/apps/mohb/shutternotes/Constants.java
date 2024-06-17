/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : Constants.java
 *  Last modified : 6/17/24, 9:46 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;


@SuppressWarnings("WeakerAccess")
public class Constants {

    // App Shared Preferences
    public static final String NOTEBOOK_SAVED_STATE = "NotebookSavedState";
    public static final String ARCHIVE_SAVED_STATE = "ArchiveSavedState";
    public static final String GEAR_LIST_SAVED_STATE = "GearListSavedState";
    public static final String GEAR_LIST_SELECTED_STATE = "GearListSelectedState";
    public static final String EDIT_GEAR_TEXT = "EditGearText";
    public static final String FLICKR_NOTE_WARNING = "FlickrNoteWarning";
    public static final String FULLSCREEN_INSTRUCTIONS = "FullscreenInstructions";

    // Lists Saved Preferences
    public static final String SIMPLE_NOTES = "SimpleNotes";
    public static final String GEAR_NOTES = "GearNotes";
    public static final String FLICKR_NOTES = "FlickrNotes";
    public static final String FLICKR_TITLE = "FlickrTitle";
    public static final String FLICKR_DESCRIPTION = "FlickrDescription";
    public static final String FLICKR_TAGS = "FlickrTags";
    public static final String GEAR_LIST = "GearList";
    public static final String GEAR_EDITED_TEXT = "GearEditedText";
    public static final String GEAR_ITEM_POSITION = "GearItemPosition";
    public static final int NULL_POSITION = -1;

    public static final String JSON_START_TIME = "startTime";
    public static final String JSON_FINISH_TIME = "finishTime";
    public static final String JSON_TEXT = "text";
    public static final String JSON_GEAR_ITEM = "gearItem";
    public static final String JSON_SELECTED = "selected";
    public static final String JSON_TITLE = "title";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_TAGS = "tags";
    public static final String JSON_LATITUDE = "latitude";
    public static final String JSON_LONGITUDE = "longitude";
    public static final String GEAR_LIST_BACKUP_FILE = "gear_list.json";

    // Date
    public static final String DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";
    public static final String DATE_PATTERN = "\\d\\d\\d\\d:\\d\\d:\\d\\d\\s\\d\\d:\\d\\d:\\d\\d";
    public static final int DATE_INIT = 0;
    public static final int DATE_TAKEN = 1;
    public static final int INITIAL_POSITION = 0;

    // Permissions request
    public static final int FINE_LOCATION_PERMISSION_REQUEST = 0;

    // Map
    public static final int MAP_HIGH_ZOOM_LEVEL = 16;
    public static final int MAP_MID_ZOOM_LEVEL = 10;
    public static final int MAP_LOW_ZOOM_LEVEL = 4;
    public static final int MAP_NONE_ZOOM_LEVEL = 0;

    public static final double MARKER_HZ_Y_OFFSET = -0.00035;
    public static final double MARKER_MZ_Y_OFFSET = -0.02;
    public static final double MARKER_LZ_Y_OFFSET = -1;
    public static final double MARKER_NZ_Y_OFFSET = -5;

    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final double DEFAULT_LATITUDE = 0;
    public static final double DEFAULT_LONGITUDE = 0;
    public static final int MS_PER_HOUR = 3600000;
    public static final String UTC = "   UTC ";

    // Lists
    public static final int LIST_ADAPTER_RESOURCE_ID = 0;
    public static final int LIST_HEAD = 0;
    public static final int LIST_HEADER_POSITION = 1;
    public static final int GRID_HEADER_POSITION = 2;
    public static final double LIST_ITEM_HEIGHT_FACTOR = 2.5;

    // Notifications
    public static final String NOTIFICATION_CHANNEL = "0";
    public static final int NOTIFICATION_SILENT_ID = 0;
    public static final int NOTIFICATION_SOUND_ID = 1;

    // Feedback
    public static final int FEEDBACK_ARRAY_SIZE = 1;

    // States
    public static final int STATE_COLOR_GREEN = 1;
    public static final int STATE_COLOR_RED = 2;

    // Keys
    public static final String KEY_FULL_SCREEN_TEXT = "Text";
    public static final String KEY_CALLER_ACTIVITY = "CallerActivity";
    public static final String KEY_FIRST_SHOW = "FirstShow";
    public static final String KEY_URL = "url";

    // Activities codes
    public static final int ACTIVITY_MAIN = 0;
    public static final int ACTIVITY_LISTS = 1;
    public static final int ACTIVITY_SIMPLE_NOTE = 2;
    public static final int ACTIVITY_GEAR_NOTE = 3;
    public static final int ACTIVITY_FLICKR_NOTE = 4;
    public static final int ACTIVITY_FLICKR_NOTES = 5;
    public static final int ACTIVITY_FLICKR_PHOTOSETS = 6;
    public static final int ACTIVITY_FLICKR_UPLOAD = 7;

    // Flickr
    public static final String FLICKR_ACCOUNT = "FlickrAccount";
    public static final int AUTH_TIMEOUT = 3;
    public static final String TOKEN = "Token";
    public static final String TOKEN_SECRET = "TokenSecret";
    public static final int TOKEN_KEY_SIZE_MIN = 9;
    public static final int TOKEN_KEY_SIZE_MAX = 11;
    public static final String FLICKR_URL = "https://flickr.com/photos/";
    public static final String PHOTOSET_ID = "PhotosetId";
    public static final String PHOTOSET_SIZE = "PhotosetSize";
    public static final int PHOTOSET_PER_PAGE = 10;

    // Characters
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String COMMA = ", ";
    public static final String COLON = ": ";
    public static final String DASH = "-";
    public static final String SLASH = "/";
    public static final String QUOTE = "\"";
    public static final String DOUBLE_QUOTE = "\"\"";
    public static final String BRACKET_LEFT = "[";
    public static final String BRACKET_RIGHT = "]";
    public static final String NEW_LINE = "\n";
    public static final String NON_ALPHA = "[^\\x30-\\x39\\x41-\\x5A\\x61-\\x7A\\x80-\\xFF]";

    // Settings
    public static final String PREF_KEY_FONT_SIZE = "FONT_SIZE";
    public static final String PREF_FONT_SIZE_SMALL = "1";
    public static final float FONT_SIZE_SMALL_SMALL = 10;
    public static final float FONT_SIZE_SMALL_MEDIUM = 15;
    public static final float FONT_SIZE_SMALL_LARGE = 20;
    public static final String PREF_FONT_SIZE_MEDIUM = "2";
    public static final float FONT_SIZE_MEDIUM_SMALL = 12;
    public static final float FONT_SIZE_MEDIUM_MEDIUM = 20;
    public static final float FONT_SIZE_MEDIUM_LARGE = 25;
    public static final String PREF_FONT_SIZE_LARGE = "3";
    public static final float FONT_SIZE_LARGE_SMALL = 15;
    public static final float FONT_SIZE_LARGE_MEDIUM = 25;
    public static final float FONT_SIZE_LARGE_LARGE = 30;

    public static final double FULL_SCREEN_TEXT_LINE_SPACING = 1.25;

    public static final String PREF_KEY_NOTIF_SOUND = "NOTIF_SOUND";
    public static final String PREF_DEF_NOTIF_SOUND = "0";
    public static final int PREF_NOTIF_SOUND_SILENT = 0;

    public static final String PREF_KEY_WHAT_SHOW = "WHAT_SHOW";
    public static final String PREF_SHOW_TITLE = "1";
    public static final String PREF_SHOW_DESCRIPTION = "2";
    public static final String PREF_SHOW_TAGS = "3";

    public static final String PREF_KEY_OVERWRITE_TAGS = "OVERWRITE_TAGS";
    public static final String PREF_REPLACE_ALL = "1";
    public static final String PREF_INSERT_BEGIN = "2";
    public static final String PREF_INSERT_END = "3";

    public static final String PREF_KEY_MAP_ZOOM_LEVEL = "MAP_ZOOM_LEVEL";
    public static final String PREF_HIGH = "3";
    public static final String PREF_MID = "2";
    public static final String PREF_LOW = "1";

    public static final String PREF_KEY_UPLOAD_LOCATION = "UPLOAD_LOCATION";
    public static final String PREF_KEY_UPLOAD_TAGS = "UPLOAD_TAGS";
    public static final String PREF_KEY_OVERWRITE_DATA = "OVERWRITE_DATA";
    public static final String PREF_KEY_ARCHIVE_NOTES = "ARCHIVE_NOTES";

    public static final int REQUEST_CODE_RINGTONE = 1;

    public static final int CREATE_FILE = 1;
    public static final int OPEN_FILE = 2;


    // Log tags
    public static final String LOG_DEBUG_TAG = "DEBUG_NOTES";
    public static final String LOG_INFO_TAG = "INFO_NOTES";
    public static final String LOG_ERROR_TAG = "ERROR_NOTES";
    public static final String LOG_EXCEPT_TAG = "EXCEPT_NOTES";

}