/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrUploadToPhotosActivity.java
 *  Last modified : 6/17/24, 9:46 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.apps.mohb.shutternotes.adapters.FlickrPhotosListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.RunInBackgroundAlertFragment;
import com.apps.mohb.shutternotes.lists.Archive;
import com.apps.mohb.shutternotes.lists.Notebook;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.photos.Exif;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.tags.TagRaw;
import com.flickr4java.flickr.tags.TagsInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;


public class FlickrUploadToPhotosActivity extends BackgroundTaskActivity implements
        RunInBackgroundAlertFragment.RunInBackgroundAlertDialogListener {

    private Collection<Photo> updatedPhotos;
    private ListView photosListView;
    private FlickrPhotosListAdapter adapter;

    private String selectedSetId;
    private int selectedSetSize;

    private Notebook notebook;
    private Archive archive;
    private ArrayList<FlickrNote> flickrNotes;
    private ArrayList<FlickrNote> selectedNotes;

    private FlickrApi flickrApi;
    private Auth auth;

    private SharedPreferences settings;

    private View progressBarView;
    private ProgressBar progressBar;
    private TextView progressRatio;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private boolean inBackground;
    private boolean uploadFinished;
    private int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_upload_to_photos);

        View listHeader = getLayoutInflater().inflate(R.layout.list_header, photosListView);
        View listFooter = getLayoutInflater().inflate(R.layout.list_footer, photosListView);

        photosListView = findViewById(R.id.photosList);

        photosListView.addHeaderView(listHeader);
        photosListView.addFooterView(listFooter);
        listHeader.setClickable(false);
        listFooter.setClickable(false);

        photosListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Photo photo = (Photo) adapterView.getAdapter().getItem(i);
            String url = photo.getUrl();
            Intent intent = new Intent(getApplicationContext(), FlickrPhotoActivity.class);
            intent.putExtra(Constants.KEY_URL, url);
            startActivity(intent);
        });

        progressBarView = findViewById(R.id.progressBarView);

        progressBar = findViewById(R.id.progressBar);
        progressRatio = findViewById(R.id.progressRatio);

        inBackground = false;

        flickrApi = new FlickrApi(getApplicationContext());

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedSetId = bundle.getString(Constants.PHOTOSET_ID);
            selectedSetSize = bundle.getInt(Constants.PHOTOSET_SIZE);
        }

        updatedPhotos = new ArrayList<>();

        notebook = Notebook.getInstance(getApplicationContext());
        archive = Archive.getInstance(getApplicationContext());

        flickrNotes = notebook.getFlickrNotes();
        selectedNotes = new ArrayList<>();

        for (int i = 0; i < flickrNotes.size(); i++) {
            FlickrNote note = flickrNotes.get(i);
            if (note.isSelected()) {
                selectedNotes.add(note);
            }
        }

        try {

            auth = flickrApi.checkAuthToken();

            if (auth == null) {
                Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
                intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_UPLOAD);
                startActivity(intent);
            } else {
                new UploadData().start();
            }

        } catch (Exception e) {
            Toast unableToCommunicate = Toast.makeText(this, R.string.toast_unable_to_communicate, Toast.LENGTH_SHORT);
            unableToCommunicate.show();
            onBackPressed();
        }

        notificationManager = getSystemService(NotificationManager.class);

        Intent intent = new Intent(this, ReturnToForegroundActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_camera_black_24dp)
                .setContentTitle(getBaseContext().getResources().getString(R.string.notify_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setNotificationSilent()
                .setOngoing(true)
                .setAutoCancel(true);

        progress = 0;

        notificationBuilder.setProgress(selectedSetSize, progress, false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        inBackground = false;

        try {
            notificationManager.cancel(Constants.NOTIFICATION_SILENT_ID);
            notificationManager.cancel(Constants.NOTIFICATION_SOUND_ID);
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }

        if (uploadFinished && updatedPhotos.isEmpty()) {
            onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground = true;
        if (progress > 0 && progress < selectedSetSize) {
            notificationManager.notify(Constants.NOTIFICATION_SILENT_ID, notificationBuilder.build());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if ((progress > 0 && progress < selectedSetSize) || inBackground) {
                RunInBackgroundAlertFragment runInBackgroundAlertFragment = new RunInBackgroundAlertFragment();
                runInBackgroundAlertFragment.show(getSupportFragmentManager(), "RunInBackgroundDialogFragment");
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onRunInBackgroundDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
        moveTaskToBack(true);
    }

    @Override
    public void onRunInBackgroundDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    private class UploadData extends BackgroundTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setMax(selectedSetSize);
            uploadFinished = false;

        }

        @Override
        protected void doInBackground() {
            super.doInBackground();

            try {

                Flickr flickr = flickrApi.getFlickrInterface();
                RequestContext.getRequestContext().setAuth(auth);
                int pages = getNumOfPages(selectedSetSize, Constants.PHOTOSET_PER_PAGE);
                for (int page = 1; page <= pages; page++) {
                    Collection<Photo> photos = flickr.getPhotosetsInterface()
                            .getPhotos(selectedSetId, Constants.PHOTOSET_PER_PAGE, page);
                    for (Photo photo : photos) {

                        progress++;
                        final int p = progress;

                        String progressText = getBaseContext().getResources().getString(R.string.text_photo_cl)
                                + Constants.SPACE + progress + Constants.SLASH + selectedSetSize;

                        if (inBackground) {
                            notificationBuilder.setProgress(selectedSetSize, progress, false);
                            notificationBuilder.setContentText(progressText);
                            notificationManager.notify(Constants.NOTIFICATION_SILENT_ID, notificationBuilder.build());
                        }

                        runOnUiThread(() -> {
                            progressBar.setProgress(p, true);
                            progressRatio.setText(progressText);
                        });

                        String photoId = photo.getId();
                        for (int i = 0; i < selectedNotes.size(); i++) {
                            FlickrNote note = selectedNotes.get(i);
                            String date = getDateTaken(photoId);
                            if (note.isInTimeInterval(date)) {
                                if (uploadDataToPhoto(photoId, note)) {
                                    // If photo was updated, added it to list
                                    updatedPhotos.add(flickrApi.getFlickrInterface()
                                            .getPhotosInterface().getPhoto(photoId));
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }

        }

        @Override
        protected void onPostExecute() {
            super.onPostExecute();

            notificationManager.cancel(Constants.NOTIFICATION_SILENT_ID);

            Intent intent = new Intent(getBaseContext(), ReturnToForegroundActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            progressBarView.setVisibility(View.INVISIBLE);

            if (adapter == null) {
                adapter = new FlickrPhotosListAdapter(getApplicationContext(), updatedPhotos);
            }

            photosListView.setAdapter(adapter);

            String updatedPhotosText;

            if (updatedPhotos.isEmpty()) {
                updatedPhotosText = getResources().getString(R.string.notify_no_photos_updated);

            } else {
                updatedPhotosText = updatedPhotos.size() + Constants.SPACE + getResources().getString(R.string.notify_photo);

                if (updatedPhotos.size() > 1) {
                    updatedPhotosText = updatedPhotosText + getResources().getString(R.string.notify_s) + Constants.SPACE
                            + getResources().getString(R.string.notify_were) + Constants.SPACE
                            + getResources().getString(R.string.notify_updated)
                            + getResources().getString(R.string.notify_end_s);
                } else {
                    updatedPhotosText = updatedPhotosText + getResources().getString(R.string.notify_was) + Constants.SPACE
                            + getResources().getString(R.string.notify_updated);
                }
            }

            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), Constants.NOTIFICATION_CHANNEL);
            notificationBuilder.setSmallIcon(R.drawable.ic_camera_black_24dp)
                    .setContentTitle(getResources().getString(R.string.notify_upload_completed))
                    .setContentText(updatedPhotosText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Uri notificationSound = Uri.parse(Objects.requireNonNull(settings.getString(Constants.PREF_KEY_NOTIF_SOUND, Constants.PREF_DEF_NOTIF_SOUND)));
                notificationBuilder.setSound(notificationSound);
            }

            if (!inBackground) {
                if (updatedPhotos.isEmpty()) {
                    Toast.makeText(getBaseContext(), R.string.notify_no_photos_updated, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {

                    Toast.makeText(getBaseContext(), updatedPhotosText, Toast.LENGTH_SHORT).show();

                    if (settings.getBoolean(Constants.PREF_KEY_ARCHIVE_NOTES, false)) {
                        int listSize = flickrNotes.size();
                        for (int i = listSize - 1; i >= 0; i--) {
                            FlickrNote note = flickrNotes.get(i);
                            if (note.isSelected()) {
                                archive.addNote(note);
                                notebook.removeFlickrNote(i);
                            }
                        }
                    }
                }

            } else {
                notificationManager.notify(Constants.NOTIFICATION_SOUND_ID, notificationBuilder.build());
            }

            try {
                notebook.saveState();
                archive.saveState();
            } catch (IOException e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }

            uploadFinished = true;

        }

    }

    private boolean uploadDataToPhoto(String photoId, FlickrNote note) throws FlickrException {

        boolean overwriteData = settings.getBoolean(Constants.PREF_KEY_OVERWRITE_DATA, false);
        String overwriteTags = settings.getString(Constants.PREF_KEY_OVERWRITE_TAGS, Constants.PREF_REPLACE_ALL);

        RequestContext.getRequestContext().setAuth(auth);
        PhotosInterface photosInterface = flickrApi.getFlickrInterface().getPhotosInterface();

        boolean wasUpdated = false;

        // If overwrite data setting is on or there is no title in the photo
        // upload title and description to photo
        if (overwriteData || photosInterface.getPhoto(photoId).getTitle().isEmpty()) {
            photosInterface.setMeta(photoId, note.getTitle(), note.getDescription());
            wasUpdated = true;
        }

        // If upload tags setting is on and there are tags in the notes upload tags to photo
        if ((settings.getBoolean(Constants.PREF_KEY_UPLOAD_TAGS, true)) && (note.getTagsArray().length > 0)) {

            String[] tagsToAdd = note.getTagsArray();
            String[] tagsOnPhoto = getPhotoTagsArray(photosInterface.getPhoto(photoId).getTags().toArray());

            // Add note's tags if there are no tags on the photo
            // or overwrite data setting is on and overwrite tags setting is replace all
            if (tagsOnPhoto.length == 0) {
                photosInterface.setTags(photoId, getNewPhotoTagsArray(tagsToAdd));

            } else if (overwriteData) {

                // If there are tags on photo and overwrite data is on
                // add tags according to overwrite tags setting
                switch (Objects.requireNonNull(overwriteTags)) {

                    case Constants.PREF_REPLACE_ALL:
                        getNewPhotoTagsArray(tagsToAdd);
                        photosInterface.setTags(photoId, getNewPhotoTagsArray(tagsToAdd));
                        break;

                    case Constants.PREF_INSERT_BEGIN:
                        getNewPhotoTagsArray(tagsToAdd, tagsOnPhoto);
                        photosInterface.setTags(photoId, getNewPhotoTagsArray(tagsToAdd, tagsOnPhoto));
                        break;

                    case Constants.PREF_INSERT_END:
                        getNewPhotoTagsArray(tagsOnPhoto, tagsToAdd);
                        photosInterface.setTags(photoId, getNewPhotoTagsArray(tagsOnPhoto, tagsToAdd));
                        break;

                }
            }

            wasUpdated = true;

        }

        // Add location info to the photo if upload location setting is on
        // and overwrite data is on or there is no location info on the photo
        if (settings.getBoolean(Constants.PREF_KEY_UPLOAD_LOCATION, true)
                && (overwriteData || photosInterface.getPhoto(photoId).getGeoData() == null)) {
            photosInterface.getGeoInterface().setLocation(photoId, note.getGeoData());
            wasUpdated = true;
        }

        return wasUpdated;

    }

    public int getNumOfPages(int photosCount, int perPage) {

        float floatDiv = (float) photosCount / perPage;
        int intDiv = photosCount / perPage;

        if (floatDiv - intDiv > 0) {
            return intDiv + 1;
        } else {
            return intDiv;
        }

    }

    public String getDateTaken(String photoId) throws FlickrException {
        Collection<Exif> exif = flickrApi.getFlickrInterface().getPhotosInterface().getExif(photoId, flickrApi.getApiSecret());
        Iterator<Exif> exifIterator = exif.iterator();
        int date = Constants.DATE_INIT;

        while (exifIterator.hasNext()) {
            Exif e = exifIterator.next();
            if (Pattern.matches(Constants.DATE_PATTERN, e.getRaw())) {
                if (date == Constants.DATE_TAKEN) {
                    return e.getRaw();
                }
                date++;
            }
        }

        return Constants.EMPTY;
    }

    public String[] getPhotoTagsArray(Object[] tags) throws FlickrException {

        TagsInterface tagsInterface = flickrApi.getFlickrInterface().getTagsInterface();

        String[] tagsStringArray = new String[tags.length];

        // Complete list of user's "raw" tags, including spaces, upper cases and non-alpha characters
        Collection<TagRaw> listUserRaw = tagsInterface.getListUserRaw();

        for (int i = 0; i < tags.length; i++) {

            // The tag retrieved from the photo, with only lower-case alpha characters
            String tag = tags[i].toString()
                    .replace("Tag [value=", Constants.EMPTY)
                    .replace(", count=0]", Constants.EMPTY);

            for (TagRaw tagRaw : listUserRaw) {
                // Get tag's raw string
                String tagString = String.valueOf(tagRaw.getRaw())
                        .replace(Constants.BRACKET_LEFT, Constants.QUOTE)
                        .replace(Constants.BRACKET_RIGHT, Constants.QUOTE);

                //  If there is more than one tag, get only the first
                if (tagString.contains(Constants.COMMA)) {
                    tagString = tagString.split(Constants.COMMA)[0].concat(Constants.QUOTE);
                }

                // The string to compare with the tag
                String tagToCompare = tagString.toLowerCase().replaceAll(Constants.NON_ALPHA, Constants.EMPTY);

                if (tag.equals(tagToCompare)) {
                    tagsStringArray[i] = tagString.replace(Constants.QUOTE, Constants.EMPTY);
                }
            }
        }

        return tagsStringArray;

    }

    public String[] getNewPhotoTagsArray(String[] tagsArray1, String[] tagsArray2) {

        String[] newTagsArray = new String[tagsArray1.length + tagsArray2.length];

        for (int i = 0; i < newTagsArray.length; i++) {
            if (i < tagsArray1.length) {
                newTagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray1[i]).concat(Constants.DOUBLE_QUOTE);
            } else {
                newTagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray2[i - tagsArray1.length]).concat(Constants.DOUBLE_QUOTE);
            }
        }

        return newTagsArray;

    }

    public String[] getNewPhotoTagsArray(String[] tagsArray) {

        for (int i = 0; i < tagsArray.length; i++) {
            tagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray[i]).concat(Constants.DOUBLE_QUOTE);
        }

        return tagsArray;

    }

}

