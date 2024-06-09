/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : FlickrPhotosetsListActivity.java
 *  Last modified : 6/8/24, 10:58 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.adapters.FlickrPhotosetsListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.AuthenticationNeededAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.ConfirmUploadAlertFragment;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.photosets.Photoset;

import java.util.Collection;


public class FlickrPhotosetsListActivity extends BackgroundTaskActivity implements
        ConfirmUploadAlertFragment.ConfirmUploadAlertDialogListener,
        AuthenticationNeededAlertFragment.AuthenticationNeededAlertDialogListener {

    private Collection<Photoset> photosets;
    private ListView photosetsListView;

    private String selectedSetId;
    private int selectedSetSize;

    private FlickrApi flickrApi;
    private Auth auth;

    private View progressSpinnerView;

    private int callerActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_photosets_list);

        try {

            flickrApi = new FlickrApi(getApplicationContext());

            View listHeader = getLayoutInflater().inflate(R.layout.list_header, photosetsListView);
            View listFooter = getLayoutInflater().inflate(R.layout.list_footer, photosetsListView);

            photosetsListView = findViewById(R.id.photosetsList);
            photosetsListView.setOnItemClickListener((adapterView, view, i, l) -> {
                ConfirmUploadAlertFragment dialogConfirm = new ConfirmUploadAlertFragment();
                dialogConfirm.show(getSupportFragmentManager(), "ConfirmUploadDialogFragment");
                Photoset photoset = (Photoset) adapterView.getAdapter().getItem(i);
                selectedSetId = photoset.getId();
                selectedSetSize = photoset.getPhotoCount();
            });

            photosetsListView.addHeaderView(listHeader);
            photosetsListView.addFooterView(listFooter);
            listHeader.setClickable(false);
            listFooter.setClickable(false);

            progressSpinnerView = findViewById(R.id.progressSpinnerView);

            callerActivity = getIntent().getIntExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTES);

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.toast_unable_to_communicate, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (photosetsListView.getAdapter() == null) {
            auth = flickrApi.checkAuthToken();
            if (auth == null) {
                if (callerActivity == Constants.ACTIVITY_FLICKR_NOTES) {
                    callerActivity = Constants.ACTIVITY_FLICKR_PHOTOSETS;
                    Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
                    intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_PHOTOSETS);
                    startActivity(intent);
                } else {
                    AuthenticationNeededAlertFragment authenticationNeeded = new AuthenticationNeededAlertFragment();
                    authenticationNeeded.show(getSupportFragmentManager(), "AuthenticationNeededDialogFragment");
                }

            } else {
                new GetPhotosets().start();
            }

        }


    }

    @Override
    public void onConfirmUploadDialogPositiveClick(DialogFragment dialog) {

        if (!selectedSetId.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), FlickrUploadToPhotosActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PHOTOSET_ID, selectedSetId);
            bundle.putInt(Constants.PHOTOSET_SIZE, selectedSetSize);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onConfirmUploadDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onAuthenticationNeededDialogPositiveClick(DialogFragment dialog) {
        Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
        intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_PHOTOSETS);
        startActivity(intent);
    }

    @Override
    public void onAuthenticationNeededDialogNegativeClick(DialogFragment dialog) {
        onBackPressed();
    }

    private class GetPhotosets extends BackgroundTask {

        protected void doInBackground() {

            try {
                String user = auth.getUser().getId();
                RequestContext.getRequestContext().setAuth(auth);
                photosets = flickrApi.getFlickrInterface().getPhotosetsInterface().getList(user).getPhotosets();
            } catch (Exception e) {
                Log.e(Constants.LOG_EXCEPT_TAG, Log.getStackTraceString(e));
            }

        }

        @Override
        protected void onPostExecute() {
            super.onPostExecute();
            progressSpinnerView.setVisibility(View.INVISIBLE);
            FlickrPhotosetsListAdapter adapter = new FlickrPhotosetsListAdapter(getApplicationContext(), photosets);
            photosetsListView.setAdapter(adapter);
        }
    }

}
