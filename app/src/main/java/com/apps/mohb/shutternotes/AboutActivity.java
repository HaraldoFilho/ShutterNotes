/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : AboutActivity.java
 *  Last modified : 6/10/24, 7:53 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.fragments.dialogs.MaterialIconsDialogFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.PrivacyPolicyDialogFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.TermsOfUseDialogFragment;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // displays app version number
        TextView version = findViewById(R.id.textAppVersion);
        String versionText = getString(R.string.version_name) + Constants.SPACE + getString(R.string.version_number);
        version.setText(versionText);

        Button buttonBmc = findViewById(R.id.buttonBmc);

        buttonBmc.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/haraldo"));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.toast_browser_not_found, Toast.LENGTH_SHORT).show();
            }
        });

    }


    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_about, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        DialogFragment dialog;

        switch (id) {

            // Feedback
            case R.id.action_feedback:
                String[] addresses = new String[Constants.FEEDBACK_ARRAY_SIZE];
                addresses[Constants.LIST_HEAD] = getString(R.string.info_feedback_email);
                String subject = getString(R.string.action_feedback)
                        + Constants.SPACE + getString(R.string.action_about_application)
                        + Constants.SPACE + getString(R.string.info_app_name);
                composeEmail(addresses, subject);
                break;

            // Terms of use
            case R.id.action_terms_of_use:
                dialog = new TermsOfUseDialogFragment();
                dialog.show(getSupportFragmentManager(), "TermsOfUseDialogFragment");
                break;

            // Privacy policy
            case R.id.action_privacy_policy:
                dialog = new PrivacyPolicyDialogFragment();
                dialog.show(getSupportFragmentManager(), "PrivacyPolicyDialogFragment");
                break;

            // Icons attribution
            case R.id.action_material_icons:
                dialog = new MaterialIconsDialogFragment();
                dialog.show(getSupportFragmentManager(), "MaterialIconsDialogFragment");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    // Compose a e-mail to send a question
    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        startActivity(intent);
    }

}
